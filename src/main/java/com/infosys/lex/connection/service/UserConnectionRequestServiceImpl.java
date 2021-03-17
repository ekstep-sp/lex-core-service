package com.infosys.lex.connection.service;

import com.infosys.lex.common.service.NotificationService;
import com.infosys.lex.common.util.LexConstants;
import com.infosys.lex.connection.dto.ConnectionRequestDTO;
import com.infosys.lex.connection.dto.ConnectionRequestUpdateDTO;

import static com.infosys.lex.connection.postgresdb.UserConnectionRequest.UserConnectionRequestStatus;

import com.infosys.lex.connection.postgresdb.UserConnectionRequest;
import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.repository.UserConnectionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Validated
public class UserConnectionRequestServiceImpl implements UserConnectionRequestService {

	private final UserConnectionRequestRepository userConnectionRequestRepository;

	private final UserConnectionService userConnectionService;

	private final NotificationService notificationService;

	private final Environment env;

	public UserConnectionRequestServiceImpl(UserConnectionRequestRepository userConnectionRequestRepository, UserConnectionService userConnectionService, NotificationService notificationService, Environment env) {
		this.userConnectionRequestRepository = userConnectionRequestRepository;
		this.userConnectionService = userConnectionService;
		this.notificationService = notificationService;
		this.env = env;
	}

	@Override
	public ResponseEntity<?> getPendingRequests(UUID userid, Pageable pageable) {
		return new ResponseEntity<>(userConnectionRequestRepository.findAllByStatusAndRequestedByOrRequestedTo(
				UserConnectionRequestStatus.Pending, userid, userid, pageable
		), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> sendConnectionRequest(@Valid ConnectionRequestDTO connectionRequestDTO) {
		if (connectionRequestDTO.getRequestedTo().equals(connectionRequestDTO.getRequestedBy())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LexConstants.INVALID_USER_IDS_MESSAGE);
		}
		Optional<UserConnections> userConnectionOptional = userConnectionService.getOptionalConnection(
				connectionRequestDTO.getRequestedBy(),
				connectionRequestDTO.getRequestedTo());
		if (userConnectionOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, LexConstants.CONNECTION_ALREADY_EXISTS);
		}
		UserConnectionRequest userConnectionRequest = new UserConnectionRequest();
		Integer requestLimit = env.getProperty(connectionRequestDTO.getRootOrg() + LexConstants.ENV_REQUEST_LIMIT_KEY, Integer.class);
		requestLimit = requestLimit == null ? 3 : requestLimit;
		Optional<UserConnectionRequest> opt = userConnectionRequestRepository.findByRequestedByAndRequestedTo(connectionRequestDTO.getRequestedBy(), connectionRequestDTO.getRequestedTo());
		if (opt.isPresent()) {
			userConnectionRequest = opt.get();
			if (userConnectionRequest.getRequestCount() >= requestLimit) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, LexConstants.REQUEST_LIMIT_MESSAGE);
			}
			if (!(userConnectionRequest.getStatus().equals(UserConnectionRequestStatus.Rejected) || userConnectionRequest.getStatus().equals(UserConnectionRequestStatus.Deleted))) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LexConstants.INVALID_REQUEST_MESSAGE);
			}
		} else {
			userConnectionRequest.setRequestId(UUID.randomUUID());
			userConnectionRequest.setRequestedBy(connectionRequestDTO.getRequestedBy());
			userConnectionRequest.setRequestedTo(connectionRequestDTO.getRequestedTo());
		}
		userConnectionRequest.setComment(connectionRequestDTO.getComment());
		userConnectionRequest.setStatus(UserConnectionRequestStatus.Pending);
		userConnectionRequest.incrementRequestCount();
		userConnectionRequest = userConnectionRequestRepository.save(userConnectionRequest);
		UserConnectionRequest finalUserConnectionRequest = userConnectionRequest;
		CompletableFuture.runAsync(() -> notificationService.sendNotificationForNewConnectionRequest(
				connectionRequestDTO,
				finalUserConnectionRequest.getRequestId()
		));
		Map<String, Object> resp = new HashMap<>();
		resp.put(LexConstants.REQUEST_ID, userConnectionRequest.getRequestId());
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<?> updateConnectionRequest(@Valid ConnectionRequestUpdateDTO connectionRequestUpdateDTO) {
		Optional<UserConnectionRequest> optionalRequest = userConnectionRequestRepository.findById(connectionRequestUpdateDTO.getRequestId());
		UserConnectionRequest request = optionalRequest.
				orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, LexConstants.REQUEST_NOT_FOUND_MESSAGE));
		if (!request.getStatus().equals(UserConnectionRequestStatus.Pending)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LexConstants.REQUEST_NOT_PENDING_MESSAGE);
		}
		UUID verifyId = request.getRequestedTo();
		String eventId = null;
		if (connectionRequestUpdateDTO.getAction().equals(ConnectionRequestUpdateDTO.Action.Accept)) {
			request.setStatus(UserConnectionRequestStatus.Accepted);
			eventId = LexConstants.ACCEPT_REQUEST_EVENT_ID;
		} else if (connectionRequestUpdateDTO.getAction().equals(ConnectionRequestUpdateDTO.Action.Reject)) {
			request.setStatus(UserConnectionRequestStatus.Rejected);
			eventId = LexConstants.REJECT_REQUEST_EVENT_ID;
		} else if (connectionRequestUpdateDTO.getAction().equals(ConnectionRequestUpdateDTO.Action.Withdraw)) {
			request.setStatus(UserConnectionRequestStatus.Deleted);
			verifyId = request.getRequestedBy();
		}
		if (!verifyId.equals(connectionRequestUpdateDTO.getActorId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, LexConstants.CHANGE_REQUEST_STATUS_NOT_ALLOWED_MESSAGE);
		}
		userConnectionRequestRepository.save(request);
		UserConnections connection = null;
		if (request.getStatus().equals(UserConnectionRequestStatus.Accepted)) {
			connection = userConnectionService.addConnection(request.getRequestedTo(), request.getRequestedBy());
		}
		String finalEventId = eventId;
		CompletableFuture.runAsync(() -> notificationService.sendNotificationForUpdateConnectionRequest(
				connectionRequestUpdateDTO.getRootOrg(),
				request.getRequestedTo(),
				request.getRequestedBy(),
				finalEventId
		));
		return new ResponseEntity<>(connection, connection == null ? HttpStatus.NO_CONTENT : HttpStatus.OK);
	}
}
