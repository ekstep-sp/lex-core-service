package com.infosys.lex.connection.repository;

import com.infosys.lex.connection.postgresdb.UserConnectionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserConnectionRequestRepository extends JpaRepository<UserConnectionRequest, UUID> {
	Optional<UserConnectionRequest> findByRequestedByAndRequestedTo(UUID requestedBy, UUID requestedTo);

	@Query("Select u from UserConnectionRequest u where u.status=?1 and (u.requestedBy=?2 or u.requestedTo = ?3)")
	Page<UserConnectionRequest> findAllByStatusAndRequestedByOrRequestedTo(UserConnectionRequest.UserConnectionRequestStatus status, UUID requestedBy, UUID requestedTo, Pageable pageable);
}
