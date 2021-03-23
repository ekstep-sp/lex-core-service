package com.infosys.lex.common.service;

import com.infosys.lex.connection.dto.ConnectionRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationService {
	void sendNotificationForUpdateConnectionRequest(String rootOrg, UUID sender, UUID recipient, String eventId);

	void sendNotificationForNewConnectionRequest(ConnectionRequestDTO connectionRequestDTO, UUID requestId);

	boolean sendNotificationToAdminForNewUser(String rootOrg, String userId);

	boolean sendNotification(String rootOrg, String eventId, Map<String,Object> tagValues, Map<String,Object> targetData, Map<String,List<String>> recipients);
}
