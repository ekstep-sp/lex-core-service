package com.infosys.lex.connection.service;

import com.infosys.lex.connection.dto.ConnectionRequestDTO;
import com.infosys.lex.connection.dto.ConnectionRequestUpdateDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface UserConnectionRequestService {
	ResponseEntity<?> getPendingRequests(UUID userid, Pageable pageable);

	ResponseEntity<?> sendConnectionRequest(@Valid ConnectionRequestDTO connectionRequestDTO);

	ResponseEntity<?> updateConnectionRequest(@Valid ConnectionRequestUpdateDTO connectionRequestUpdateDTO);
}
