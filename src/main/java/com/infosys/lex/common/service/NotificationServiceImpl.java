package com.infosys.lex.common.service;

import com.infosys.lex.common.util.LexConstants;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.connection.dto.ConnectionRequestDTO;
import com.infosys.lex.core.exception.ApplicationLogicError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final RestTemplate restTemplate;

	private final PidUserService pidUserService;

	@Value("${notification.signup.event-id}")
	String newSignUpEventId;

	@Value("${notification.signup.recipient}")
	String recipientKey;

	@Value("${users.admin.wid}")
	String adminWid;

	private final Environment env;

	private final LexServerProperties lexServerProperties;

	public NotificationServiceImpl(RestTemplate restTemplate, PidUserService pidUserService, Environment env, LexServerProperties lexServerProperties) {
		this.restTemplate = restTemplate;
		this.pidUserService = pidUserService;
		this.env = env;
		this.lexServerProperties = lexServerProperties;
	}

	@Override
	public boolean sendNotificationToAdminForNewUser(String rootOrg, String userId) {
		List<Map<String, Object>> usersInfo = pidUserService.getUserInfoFromPid(rootOrg, Collections.singletonList(userId), Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.DEPARTMENT_NAME, PIDConstants.UUID));
		if (!usersInfo.isEmpty()) {
			Map<String, Object> userData = usersInfo.get(0);
			Map<String, Object> targetData = new HashMap<>();
			targetData.put("#userName", (userData.getOrDefault(PIDConstants.FIRST_NAME, "").toString()
					+ " " + userData.getOrDefault(PIDConstants.LAST_NAME, "").toString()).trim());
			targetData.put("#org", userData.getOrDefault(PIDConstants.DEPARTMENT_NAME, ""));
			targetData.put("#changeRoleUrl", lexServerProperties.getUserDashboardUrl());
			Map<String, List<String>> recipients = Collections.singletonMap(recipientKey, Collections.singletonList(adminWid));
			return sendNotification(rootOrg, newSignUpEventId, targetData, recipients);
		}
		return false;
	}

	@Override
	public void sendNotificationForNewConnectionRequest(ConnectionRequestDTO connectionRequestDTO, UUID requestId) {
		List<Map<String, Object>> usersInfo = pidUserService.getUserInfoFromPid(connectionRequestDTO.getRootOrg(), Arrays.asList(connectionRequestDTO.getRequestedBy().toString(), connectionRequestDTO.getRequestedTo().toString()), Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.UUID));
		Map<String, Map<String, Object>> usersInfoMap = usersInfo.stream().collect(Collectors.toMap(user -> user.get(PIDConstants.UUID).toString(), user -> user));
		if (!usersInfo.isEmpty()) {
			Map<String, Object> requestedBy = usersInfoMap.get(connectionRequestDTO.getRequestedBy().toString());
			Map<String, Object> requestedTo = usersInfoMap.get(connectionRequestDTO.getRequestedTo().toString());
			Map<String, Object> targetData = new HashMap<>();
			targetData.put(LexConstants.HASH_REQUESTED_BY, (requestedBy.getOrDefault(PIDConstants.FIRST_NAME, "").toString()
					+ " " + requestedBy.getOrDefault(PIDConstants.LAST_NAME, "").toString()).trim());
			targetData.put(LexConstants.HASH_REQUESTED_TO, (requestedTo.getOrDefault(PIDConstants.FIRST_NAME, "").toString()
					+ " " + requestedTo.getOrDefault(PIDConstants.LAST_NAME, "").toString()).trim());
			String domainUrl = Objects.requireNonNull(env.getProperty(connectionRequestDTO.getRootOrg() + LexConstants.ENV_DOMAIN_URL_KEY));
			String acceptUrl = LexConstants.ACCEPT_URL.replace(LexConstants.HASH_DOMAIN_URL, domainUrl).replace(LexConstants.HASH_REQUEST_ID, requestId.toString());
			String rejectUrl = LexConstants.REJECT_URL.replace(LexConstants.HASH_DOMAIN_URL, domainUrl).replace(LexConstants.HASH_REQUEST_ID, requestId.toString());
			targetData.put(LexConstants.HASH_ACCEPT_URL, acceptUrl);
			targetData.put(LexConstants.HASH_REJECT_URL, rejectUrl);
			targetData.put(LexConstants.HASH_COMMENT, connectionRequestDTO.getComment());
			Map<String, List<String>> recipients = Collections.singletonMap(LexConstants.REQUESTED_TO, Collections.singletonList(connectionRequestDTO.getRequestedTo().toString()));
			sendNotification(connectionRequestDTO.getRootOrg(), LexConstants.NEW_REQUEST_EVENT_ID, targetData, recipients);
		}
	}

	@Override
	public void sendNotificationForUpdateConnectionRequest(String rootOrg, UUID actor, UUID recipient, String eventId) {
		if (eventId == null) {
			return;
		}
		List<Map<String, Object>> usersInfo = pidUserService.getUserInfoFromPid(rootOrg, Arrays.asList(actor.toString(), recipient.toString()), Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.DEPARTMENT_NAME, PIDConstants.UUID, PIDConstants.EMAIL));
		Map<String, Map<String, Object>> usersInfoMap = usersInfo.stream().collect(Collectors.toMap(user -> user.get(PIDConstants.UUID).toString(), user -> user));
		if (!usersInfo.isEmpty()) {
			Map<String, Object> requestedTo = usersInfoMap.get(actor.toString());
			Map<String, Object> requestedBy = usersInfoMap.get(recipient.toString());
			Map<String, Object> targetData = new HashMap<>();
			targetData.put(LexConstants.HASH_REQUESTED_BY, (requestedBy.getOrDefault(PIDConstants.FIRST_NAME, "").toString()
					+ " " + requestedBy.getOrDefault(PIDConstants.LAST_NAME, "").toString()).trim());
			targetData.put(LexConstants.HASH_REQUESTED_TO, (requestedTo.getOrDefault(PIDConstants.FIRST_NAME, "").toString()
					+ " " + requestedTo.getOrDefault(PIDConstants.LAST_NAME, "").toString()).trim());
			targetData.put(LexConstants.HASH_CONNECTION_EMAIL, requestedTo.getOrDefault(PIDConstants.EMAIL, ""));
			Map<String, List<String>> recipients = Collections.singletonMap(LexConstants.REQUESTED_BY, Collections.singletonList(recipient.toString()));
			sendNotification(rootOrg, eventId, targetData, recipients);
		}
	}

	public boolean sendNotification(String rootOrg, String eventId, Map<String, Object> targetData, Map<String, List<String>> recipients) {
		String url = lexServerProperties.getNotificationServiceScheme() + lexServerProperties.getNotificationServiceIp() + ":" + lexServerProperties.getNotificationServicePort() + lexServerProperties.getSendNotificationEndpoint();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("event-id", eventId);
		requestBody.put("tag-value-pair", targetData);
		requestBody.put("recipients", recipients);
		HttpHeaders headers = new HttpHeaders();
		headers.set("rootOrg", rootOrg);
		headers.set("org", rootOrg);
		try {
			HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpStatusCodeException e) {
			throw new ApplicationLogicError("user info service(pid) response status " + e.getStatusCode() +
					" message " + e.getResponseBodyAsString());
		} catch (Exception e) {
			throw new ApplicationLogicError("Could not Parse user pid data");
		}
	}
}
