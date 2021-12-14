package com.infosys.lex.areaofexpertise.service;

import com.infosys.lex.areaofexpertise.bodhi.repo.AreaOfExpertiseCassandraRepo;
import com.infosys.lex.areaofexpertise.entities.AreaOfExpertise;
import com.infosys.lex.areaofexpertise.entities.AreaOfExpertiseKey;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.InvalidDataInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AreaOfExpertiseServiceImpl implements AreaOfExpertiseService {

    @Autowired
    AreaOfExpertiseCassandraRepo areaOfExpertiseCassandraRepo;

    @Autowired
    UserUtilityService userUtilityService;

    @Override
    public void upsertAreaOfExpertise(String rootOrg, String userId, Map<String, Object> areaOfExpertiseMap) {

        if (areaOfExpertiseMap.get("areaOfExpertise") == null || areaOfExpertiseMap.get("areaOfExpertise").toString().isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfExpertise");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfExpertiseKey areaOfExpertiseKey = new AreaOfExpertiseKey(rootOrg, userId);
        String areaOfExpertise = areaOfExpertiseMap.get("areaOfExpertise").toString().trim();
        Timestamp currentDate = new Timestamp(new Date().getTime());
        Optional<AreaOfExpertise> areaOfExpertiseObject = areaOfExpertiseCassandraRepo.findById(areaOfExpertiseKey);

        if (!areaOfExpertiseObject.isPresent()) {
            // Add User if user does not exist
            Set<String> setOfAreaOfExpertise = new HashSet<String>();
            setOfAreaOfExpertise.add(areaOfExpertise);
            AreaOfExpertise newUser = new AreaOfExpertise(areaOfExpertiseKey, setOfAreaOfExpertise, currentDate, currentDate);
            areaOfExpertiseCassandraRepo.save(newUser);
        } else {
            AreaOfExpertise existingUser = areaOfExpertiseObject.get();
            // User already exists so update interest
            Set<String> setToBeUpdated = existingUser.getAreaOfExpertise();
            if (setToBeUpdated == null || setToBeUpdated.isEmpty()) {
                setToBeUpdated = new HashSet<String>();
            }
            setToBeUpdated.add(areaOfExpertise);
            existingUser.setUpdatedOn(currentDate);
            areaOfExpertiseCassandraRepo.save(existingUser);
        }
    }

    @Override
    public void upsertMultipleAreaOfExpertise(String rootOrg, String userId, Map<String, Object[]> areaOfExpertiseMap) {

        if (areaOfExpertiseMap.get("areaOfExpertise") == null || Arrays.toString(areaOfExpertiseMap.get("areaOfExpertise")).isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfExpertise");
        }

        // Validating User
       if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfExpertiseKey areaOfExpertiseKey = new AreaOfExpertiseKey(rootOrg, userId);
        Object[] areaOfExpertiseArray = areaOfExpertiseMap.get("areaOfExpertise");
        Timestamp currentDate = new Timestamp(new Date().getTime());
        Optional<AreaOfExpertise> areaOfExpertiseObject = areaOfExpertiseCassandraRepo.findById(areaOfExpertiseKey);

        if (!areaOfExpertiseObject.isPresent()) {
            // Add User if user does not exist
            Set<String> setOfAreaOfExpertise = new HashSet<String>();

            for (Object areaOfExpertise : areaOfExpertiseArray) {
                setOfAreaOfExpertise.add(areaOfExpertise.toString());
            }
            AreaOfExpertise newUser = new AreaOfExpertise(areaOfExpertiseKey, setOfAreaOfExpertise, currentDate, currentDate);
            areaOfExpertiseCassandraRepo.save(newUser);
        } else {
            AreaOfExpertise existingUser = areaOfExpertiseObject.get();
            // User already exists so update interest
            Set<String> setToBeUpdated = existingUser.getAreaOfExpertise();
            if (setToBeUpdated == null || setToBeUpdated.isEmpty()) {
                setToBeUpdated = new HashSet<String>();
            }
            for (Object areaOfExpertise : areaOfExpertiseArray) {
                setToBeUpdated.add(areaOfExpertise.toString());
            }
            existingUser.setUpdatedOn(currentDate);
            areaOfExpertiseCassandraRepo.save(existingUser);
        }
    }

    @Override
    public Map<String, Object> getAreaOfExpertise(String rootOrg, String userId) {

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        Map<String, Object> resultList = new HashMap<String, Object>();

        Optional<AreaOfExpertise> cassandraObject = areaOfExpertiseCassandraRepo.findById(new AreaOfExpertiseKey(rootOrg, userId));
        if (!cassandraObject.isPresent()) {
            resultList.put("areaOfExpertise", Collections.emptyList());
            return resultList;
        }
        if (cassandraObject.get().getAreaOfExpertise() == null) {
            resultList.put("areaOfExpertise", Collections.emptyList());
        } else {
            resultList.put("areaOfExpertise", cassandraObject.get().getAreaOfExpertise());
        }

        return resultList;
    }

    @Override
    public void deleteAreaOfExpertise(String rootOrg, String userId, Map<String, Object> areaOfExpertiseMap) {

       if (areaOfExpertiseMap.get("areaOfExpertise") == null || areaOfExpertiseMap.get("areaOfExpertise").toString().isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfExpertise");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfExpertiseKey areaOfExpertiseKey = new AreaOfExpertiseKey(rootOrg, userId);
        String areaOfExpertise = areaOfExpertiseMap.get("areaOfExpertise").toString();
        Optional<AreaOfExpertise> areaOfExpertiseCassandra = areaOfExpertiseCassandraRepo.findById(areaOfExpertiseKey);
        Date dateCreatedOn = new Date();
        Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());

        if (!areaOfExpertiseCassandra.isPresent()) {
            throw new InvalidDataInputException("areaOfExpertise.notPresent");
        }
        if (!areaOfExpertiseCassandra.get().getAreaOfExpertise().contains(areaOfExpertise)) {
            throw new InvalidDataInputException("areaOfExpertise.doesNotExist");
        }

        if (areaOfExpertiseCassandra.get().getAreaOfExpertise().size() == 1) {
            areaOfExpertiseCassandraRepo.deleteById(areaOfExpertiseKey);
        } else {
            areaOfExpertiseCassandra.get().setUpdatedOn(timeCreatedOn);
            areaOfExpertiseCassandra.get().getAreaOfExpertise().remove(areaOfExpertise);
            areaOfExpertiseCassandraRepo.save(areaOfExpertiseCassandra.get());
        }

    }

    @Override
    public void deleteMultipleAreaOfExpertise(String rootOrg, String userId, Map<String, Object[]> areaOfExpertiseMap) {

        if (areaOfExpertiseMap.get("areaOfExpertise") == null || Arrays.toString(areaOfExpertiseMap.get("areaOfExpertise")).isEmpty()) {
            throw new InvalidDataInputException("invalid.areaOfExpertise");
        }

        // Validating User
        if (!userUtilityService.validateUser(rootOrg, userId)) {
            throw new InvalidDataInputException("invalid.user");
        }

        AreaOfExpertiseKey areaOfExpertiseKey = new AreaOfExpertiseKey(rootOrg, userId);
        Object[] areaOfExpertiseArray = areaOfExpertiseMap.get("areaOfExpertise");
        Optional<AreaOfExpertise> areaOfExpertiseCassandra = areaOfExpertiseCassandraRepo.findById(areaOfExpertiseKey);
        Timestamp currentDate = new Timestamp(new Date().getTime());

        if (!areaOfExpertiseCassandra.isPresent()) {
            throw new InvalidDataInputException("areaOfExpertise.notPresent");
        }

        for (Object areaOfExpertise : areaOfExpertiseArray) {
            if (!areaOfExpertiseCassandra.get().getAreaOfExpertise().contains(areaOfExpertise)) {
                throw new InvalidDataInputException("areaOfExpertise.doesNotExist");
            }
        }

        if (areaOfExpertiseCassandra.get().getAreaOfExpertise().size() == 1) {
            areaOfExpertiseCassandraRepo.deleteById(areaOfExpertiseKey);
        } else {
            areaOfExpertiseCassandra.get().setUpdatedOn(currentDate);
            for (Object areaOfExpertise : areaOfExpertiseArray) {
                areaOfExpertiseCassandra.get().getAreaOfExpertise().remove(areaOfExpertise);
            }
            areaOfExpertiseCassandraRepo.save(areaOfExpertiseCassandra.get());
        }

    }
}
