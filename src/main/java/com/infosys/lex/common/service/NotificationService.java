package com.infosys.lex.common.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {
	boolean sendNotificationToAdminForNewUser(String rootOrg, String userId);

	boolean sendNotification(String rootOrg, String eventId, Map<String,Object> targetData, Map<String,List<String>> recipients);
}
