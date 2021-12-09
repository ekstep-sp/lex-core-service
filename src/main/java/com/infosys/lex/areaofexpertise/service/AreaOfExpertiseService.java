package com.infosys.lex.areaofexpertise.service;

import java.util.Map;

public interface AreaOfExpertiseService {
    public void upsertAreaOfExpertise(String rootOrg, String userId, Map<String, Object> areaOfExpertise);

    public void upsertMultipleAreaOfExpertise(String rootOrg, String userId, Map<String, Object[]> areaOfExpertise);

    public Map<String, Object> getAreaOfExpertise(String rootOrg, String userId);

    public void deleteAreaOfExpertise(String rootOrg, String userId, Map<String, Object> areaOfExpertiseMap);
}
