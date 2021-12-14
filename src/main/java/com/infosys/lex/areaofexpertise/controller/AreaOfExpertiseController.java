package com.infosys.lex.areaofexpertise.controller;

import com.infosys.lex.areaofexpertise.service.AreaOfExpertiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AreaOfExpertiseController {

    @Autowired
    AreaOfExpertiseService areaOfExpertiseService;

    @PatchMapping("v3/users/{userid}/upsertAreaOfExpertise")
    public ResponseEntity<?> upsertAreaOfExpertise(@RequestHeader(value = "rootOrg") String rootOrg,
                                                   @PathVariable("userid") String userId,
                                                   @RequestBody Map<String, Object> areaOfExpertiseMap) {
        areaOfExpertiseService.upsertAreaOfExpertise(rootOrg, userId, areaOfExpertiseMap);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("v3/users/{userid}/upsertMultipleAreaOfExpertise")
    public ResponseEntity<?> upsertMultipleAreaOfExpertise(@RequestHeader(value = "rootOrg") String rootOrg,
                                                           @PathVariable("userid") String userId,
                                                           @RequestBody Map<String, Object[]> areaOfExpertiseMap) {
        areaOfExpertiseService.upsertMultipleAreaOfExpertise(rootOrg, userId, areaOfExpertiseMap);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/v3/users/{userid}/getAllAreaOfExpertise")
    public ResponseEntity<Map<String, Object>> getAreaOfExpertise(@RequestHeader(value = "rootOrg") String rootOrg,
                                                                  @NotNull @PathVariable("userid") String userId) {

        Map<String, Object> userAreasOfExpertise = new HashMap<String, Object>();
        userAreasOfExpertise = areaOfExpertiseService.getAreaOfExpertise(rootOrg, userId);
        return new ResponseEntity<Map<String, Object>>(userAreasOfExpertise, HttpStatus.OK);
    }

    @DeleteMapping("/v3/users/{userid}/deleteAreaOfExpertise")
    public ResponseEntity<String> deleteAreaOfExpertise(@RequestHeader(value = "rootOrg") String rootOrg,
                                                        @PathVariable("userid") String userId, @RequestBody Map<String, Object> areaOfExpertiseMap) {

        areaOfExpertiseService.deleteAreaOfExpertise(rootOrg, userId, areaOfExpertiseMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/v3/users/{userid}/deleteMultipleAreaOfExpertise")
    public ResponseEntity<String> deleteMultipleAreaOfExpertise(@RequestHeader(value = "rootOrg") String rootOrg,
                                                                @PathVariable("userid") String userId, @RequestBody Map<String, Object[]> areaOfExpertiseMap) {

        areaOfExpertiseService.deleteMultipleAreaOfExpertise(rootOrg, userId, areaOfExpertiseMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
