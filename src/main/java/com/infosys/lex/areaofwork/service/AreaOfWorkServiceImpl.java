package com.infosys.lex.areaofwork.service;

import com.infosys.lex.areaofwork.bodhi.repo.AreaOfWorkCassandraRepo;
import com.infosys.lex.areaofwork.entities.AreaOfWork;
import com.infosys.lex.areaofwork.entities.AreaOfWorkKey;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.InvalidDataInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AreaOfWorkServiceImpl implements AreaOfWorkService {

    @Autowired
    AreaOfWorkCassandraRepo areaOfWorkCassandraRepo;

    @Autowired
    UserUtilityService userUtilityService;

    @Override
    public void upsertAreaOfWork(String rootOrg, String userId, Map<String, Object> areaOfWorkMap) {

        if (areaOfWorkMap.get("areaOfWork") == null || areaOfWorkMap.get("areaOfWork").toString().isEmpty()) {
            throw new InvalidDataInputException("invalid.interest");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfWorkKey areaOfWorkKey = new AreaOfWorkKey(rootOrg, userId);
        String areaOfWork = areaOfWorkMap.get("areaOfWork").toString().trim();
        Timestamp currentDate = new Timestamp(new Date().getTime());
        Optional<AreaOfWork> areaOfWorkObject = areaOfWorkCassandraRepo.findById(areaOfWorkKey);

        if (!areaOfWorkObject.isPresent()) {
            // Add User if user does not exist
            Set<String> setOfAreaOfWork = new HashSet<String>();
            setOfAreaOfWork.add(areaOfWork);
            AreaOfWork newUser = new AreaOfWork(areaOfWorkKey, setOfAreaOfWork, currentDate, currentDate);
            areaOfWorkCassandraRepo.save(newUser);
        } else {
            AreaOfWork existingUser = areaOfWorkObject.get();
            // User already exists so update interest
            Set<String> setToBeUpdated = existingUser.getAreaOfWork();
            if (setToBeUpdated == null || setToBeUpdated.isEmpty()) {
                setToBeUpdated = new HashSet<String>();
            }
            setToBeUpdated.add(areaOfWork);
            existingUser.setAreaOfWork(setToBeUpdated);
            existingUser.setUpdatedOn(currentDate);
            areaOfWorkCassandraRepo.save(existingUser);
        }
    }

    @Override
    public void upsertMultipleAreaOfWork(String rootOrg, String userId, Map<String, Object[]> areaOfWorkMap) {
        if (areaOfWorkMap.get("areaOfWork") == null || Arrays.toString(areaOfWorkMap.get("areaOfWork")).isEmpty()) {
            throw new InvalidDataInputException("invalid.interest");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfWorkKey areaOfWorkKey = new AreaOfWorkKey(rootOrg, userId);
        Object[] areaOfWorkArray = areaOfWorkMap.get("areaOfWork");
        Timestamp currentDate = new Timestamp(new Date().getTime());
        Optional<AreaOfWork> areaOfWorkObject = areaOfWorkCassandraRepo.findById(areaOfWorkKey);

        if (!areaOfWorkObject.isPresent()) {
            // Add User if user does not exist
            Set<String> setOfAreaOfWork = new HashSet<String>();

            for (Object areaOfWork : areaOfWorkArray) {
                setOfAreaOfWork.add(areaOfWork.toString());
            }
            AreaOfWork newUser = new AreaOfWork(areaOfWorkKey, setOfAreaOfWork, currentDate, currentDate);
            areaOfWorkCassandraRepo.save(newUser);
        } else {
            AreaOfWork existingUser = areaOfWorkObject.get();
            // User already exists so update interest
            Set<String> setToBeUpdated = existingUser.getAreaOfWork();
            if (setToBeUpdated == null || setToBeUpdated.isEmpty()) {
                setToBeUpdated = new HashSet<String>();
            }
            for (Object areaOfWork : areaOfWorkArray) {
                setToBeUpdated.add(areaOfWork.toString());
            }
            existingUser.setUpdatedOn(currentDate);
            existingUser.setAreaOfWork(setToBeUpdated);
            areaOfWorkCassandraRepo.save(existingUser);
        }
    }

    @Override
    public Map<String, Object> getAreaOfWork(String rootOrg, String userId) {

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        Map<String, Object> resultList = new HashMap<String, Object>();

        Optional<AreaOfWork> cassandraObject = areaOfWorkCassandraRepo.findById(new AreaOfWorkKey(rootOrg, userId));
        if (!cassandraObject.isPresent()) {
            resultList.put("areaOfWork", Collections.emptyList());
            return resultList;
        }
        if (cassandraObject.get().getAreaOfWork() == null) {
            resultList.put("areaOfWork", Collections.emptyList());
        } else {
            resultList.put("areaOfWork", cassandraObject.get().getAreaOfWork());
        }

        return resultList;
    }

    @Override
    public void deleteAreaOfWork(String rootOrg, String userId, Map<String, Object> areaOfWorkMap) {

        if (areaOfWorkMap.get("areaOfWork") == null || areaOfWorkMap.get("areaOfWork").toString().isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfWork");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfWorkKey areaOfWorkKey = new AreaOfWorkKey(rootOrg, userId);
        String areaOfWork = areaOfWorkMap.get("areaOfWork").toString();
        Optional<AreaOfWork> areaOfWorkCassandra = areaOfWorkCassandraRepo.findById(areaOfWorkKey);
        Date dateCreatedOn = new Date();
        Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());

        if (!areaOfWorkCassandra.isPresent()) {
            throw new InvalidDataInputException("areaOfWork.notPresent");
        }
        if (!areaOfWorkCassandra.get().getAreaOfWork().contains(areaOfWork)) {
            throw new InvalidDataInputException("areaOfWork.doesNotExist");
        }

        if (areaOfWorkCassandra.get().getAreaOfWork().size() == 1) {
            areaOfWorkCassandraRepo.deleteById(areaOfWorkKey);
        } else {
            areaOfWorkCassandra.get().setUpdatedOn(timeCreatedOn);
            areaOfWorkCassandra.get().getAreaOfWork().remove(areaOfWork);
            areaOfWorkCassandraRepo.save(areaOfWorkCassandra.get());
        }

    }

    @Override
    public void deleteMultipleAreaOfWork(String rootOrg, String userId, Map<String, Object[]> areaOfWorkMap) {

        if (areaOfWorkMap.get("areaOfWork") == null || Arrays.toString(areaOfWorkMap.get("areaOfWork")).isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfWork");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfWorkKey areaOfWorkKey = new AreaOfWorkKey(rootOrg, userId);
        Object[] areaOfWorkArray = areaOfWorkMap.get("areaOfWork");
        Optional<AreaOfWork> areaOfWorkCassandra = areaOfWorkCassandraRepo.findById(areaOfWorkKey);
        Timestamp currentDate = new Timestamp(new Date().getTime());

        if (!areaOfWorkCassandra.isPresent()) {
            throw new InvalidDataInputException("areaOfWork.notPresent");
        }

        for (Object areaOfWork : areaOfWorkArray) {
            if (!areaOfWorkCassandra.get().getAreaOfWork().contains(areaOfWork)) {
                throw new InvalidDataInputException("areaOfWork.doesNotExist");
            }
        }

        if (areaOfWorkCassandra.get().getAreaOfWork() != null &&
                areaOfWorkCassandra.get().getAreaOfWork().size() == 1) {
            areaOfWorkCassandraRepo.deleteById(areaOfWorkKey);
        } else {
            areaOfWorkCassandra.get().setUpdatedOn(currentDate);
            for (Object areaOfWork : areaOfWorkArray) {
                areaOfWorkCassandra.get().getAreaOfWork().remove(areaOfWork);
            }
            areaOfWorkCassandraRepo.save(areaOfWorkCassandra.get());
        }

    }
}
