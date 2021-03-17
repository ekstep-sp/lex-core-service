package com.infosys.lex.connection.controller;

import com.infosys.lex.connection.dto.ConnectionRequestDTO;
import com.infosys.lex.connection.dto.ConnectionRequestUpdateDTO;
import com.infosys.lex.connection.service.UserConnectionRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserConnectionRequestController {

    private final UserConnectionRequestService userConnectionRequestService;

    public UserConnectionRequestController(UserConnectionRequestService userConnectionRequestService) {
        this.userConnectionRequestService = userConnectionRequestService;
    }

    @PostMapping("/v1/user/connection/requests")
    public ResponseEntity<?> createConnectionRequest(@RequestHeader String rootOrg, @RequestBody ConnectionRequestDTO connectionRequestDTO) {
        connectionRequestDTO.setRootOrg(rootOrg);
        return userConnectionRequestService.sendConnectionRequest(connectionRequestDTO);
    }

    @PatchMapping("/v1/user/connection/requests")
    public ResponseEntity<?> updateConnectionRequest(@RequestHeader String rootOrg, @RequestBody ConnectionRequestUpdateDTO connectionRequestUpdateDTO) {
        connectionRequestUpdateDTO.setRootOrg(rootOrg);
        return userConnectionRequestService.updateConnectionRequest(connectionRequestUpdateDTO);
    }

    @GetMapping("/v1/user/connection/pending-requests/{userId}")
    public ResponseEntity<?> getPendingRequests(@PathVariable("userId") UUID userId, Pageable pageable) {
        return userConnectionRequestService.getPendingRequests(userId, pageable);
    }

}
