package com.infosys.lex.common.service;

import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	PidUserService pidUserService;

	@Value("${notification.signup.event-id}")
	String newSignUpEventId;

	@Value("${notification.signup.recipient}")
	String recipientKey;

	@Value("${users.admin.wid}")
	String adminWid;

	@Autowired
	LexServerProperties lexServerProperties;

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
