package com.infosys.lex.areaofwork.service;

import java.util.Map;

public interface AreaOfWorkService {
    public void upsertAreaOfWork(String rootOrg, String userId, Map<String, Object> areaOfWork);

    public void upsertMultipleAreaOfWork(String rootOrg, String userId, Map<String, Object[]> areaOfWork);

    public Map<String, Object> getAreaOfWork(String rootOrg, String userId);

    public void deleteAreaOfWork(String rootOrg, String userId, Map<String, Object> areaOfWorkMap);

    public void deleteMultipleAreaOfWork(String rootOrg, String userId, Map<String, Object[]> areaOfWorkMap);
}
