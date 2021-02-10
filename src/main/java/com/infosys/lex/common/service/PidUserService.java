package com.infosys.lex.common.service;

import java.util.List;
import java.util.Map;

public interface PidUserService {
	List<Map<String, Object>> getUserInfoFromPid(String rootOrg, List<String> userIds, List<String> sourceFields);
}
