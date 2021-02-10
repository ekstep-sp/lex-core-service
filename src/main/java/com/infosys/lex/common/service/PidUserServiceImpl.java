package com.infosys.lex.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PidUserServiceImpl implements PidUserService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties lexServerProperties;

	public List<Map<String, Object>> getUserInfoFromPid(String rootOrg, List<String> userIds, List<String> sourceFields) {
		String url = lexServerProperties.getPidServiceScheme() + lexServerProperties.getPidIp() + ":" + lexServerProperties.getPidPort() + lexServerProperties.getUserDetailsEndpoint();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("source_fields", sourceFields);
		Map<String, Object> conditions = new HashMap<>();
		conditions.put(PIDConstants.ROOT_ORG, rootOrg);
		requestBody.put("conditions", conditions);
		requestBody.put("values", userIds);
		try {
			List<?> usersList = restTemplate.postForObject(url, requestBody, List.class);
			return new ObjectMapper().convertValue(usersList, new TypeReference<List<Map>>(){});
		} catch (HttpStatusCodeException e) {
			throw new ApplicationLogicError("user info service(pid) response status " + e.getStatusCode() +
					" message " + e.getResponseBodyAsString());
		} catch (Exception e) {
			throw new ApplicationLogicError("Could not Parse user pid data");
		}
	}
}
