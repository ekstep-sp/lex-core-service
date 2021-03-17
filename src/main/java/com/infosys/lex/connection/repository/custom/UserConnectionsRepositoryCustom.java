package com.infosys.lex.connection.repository.custom;

import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.postgresdb.UserConnectionsView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserConnectionsRepositoryCustom {
	Page<UserConnectionsView> findAllByConnectedFromAndStatus(UUID connectedFrom,UserConnections.UserConnectionStatus status, Pageable pageable);
}
