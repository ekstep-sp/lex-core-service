package com.infosys.lex.connection.repository;

import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.repository.custom.UserConnectionsRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserConnectionsRepository extends JpaRepository<UserConnections, UUID> , UserConnectionsRepositoryCustom {
	Optional<UserConnections> findByConnectedFromAndConnectedTo(UUID connectedFrom, UUID connectedTo);
}
