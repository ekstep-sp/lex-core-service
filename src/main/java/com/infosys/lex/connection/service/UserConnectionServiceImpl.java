package com.infosys.lex.connection.service;

import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.postgresdb.UserConnectionsView;
import com.infosys.lex.connection.repository.UserConnectionsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class UserConnectionServiceImpl implements UserConnectionService {

	private final UserConnectionsRepository userConnectionsRepository;

	public UserConnectionServiceImpl(UserConnectionsRepository userConnectionsRepository) {
		this.userConnectionsRepository = userConnectionsRepository;
	}

	@Override
	public UserConnections addConnection(UUID connectedFrom, UUID connectedTo) {
		UserConnections connection = userConnectionsRepository.findByConnectedFromAndConnectedTo(connectedFrom, connectedTo)
				.orElse(new UserConnections(UUID.randomUUID(),
						connectedFrom,
						connectedTo,
						null, null, null));
		UserConnections reverseConnection = userConnectionsRepository.findByConnectedFromAndConnectedTo(connectedTo, connectedFrom)
				.orElse(new UserConnections(UUID.randomUUID(),
						connectedTo,
						connectedFrom,
						null, null, null));
		connection.setStatus(UserConnections.UserConnectionStatus.Active);
		reverseConnection.setStatus(UserConnections.UserConnectionStatus.Active);
		userConnectionsRepository.saveAll(Arrays.asList(connection, reverseConnection));
		return connection;
	}

	public Page<UserConnectionsView> getConnections(UUID connectedFrom, UserConnections.UserConnectionStatus status, Pageable pageable) {
		return userConnectionsRepository.findAllByConnectedFromAndStatus(connectedFrom, status, pageable);
	}
}
