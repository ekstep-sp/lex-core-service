package com.infosys.lex.connection.controller;

import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.service.UserConnectionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserConnectionController {

    private final UserConnectionService userConnectionService;

    public UserConnectionController(UserConnectionService userConnectionService) {
        this.userConnectionService = userConnectionService;
    }

    @GetMapping("/v1/user/connections/{connectedFrom}/{status}")
    public ResponseEntity<?> getUserConnections(@PathVariable("connectedFrom") UUID connectedFrom, @PathVariable("status") UserConnections.UserConnectionStatus status, Pageable pageable) {
        return new ResponseEntity<>(userConnectionService.getConnections(connectedFrom, status, pageable), HttpStatus.OK);
    }

}
