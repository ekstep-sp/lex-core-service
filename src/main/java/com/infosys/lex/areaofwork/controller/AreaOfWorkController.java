package com.infosys.lex.areaofwork.controller;

import com.infosys.lex.areaofwork.service.AreaOfWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AreaOfWorkController {

    @Autowired
    AreaOfWorkService areaOfWorkService;

    @PatchMapping("v3/users/{userid}/upsertAreaOfWork")
    public ResponseEntity<?> upsertAreaOfWork(@RequestHeader(value = "rootOrg") String rootOrg,
                                              @PathVariable("userid") String userId,
                                              @RequestBody Map<String, Object> areaOfWorkMap) {
        areaOfWorkService.upsertAreaOfWork(rootOrg, userId, areaOfWorkMap);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("v3/users/{userid}/upsertMultipleAreaOfWork")
    public ResponseEntity<?> upsertMultipleAreaOfWork(@RequestHeader(value = "rootOrg") String rootOrg,
                                                      @PathVariable("userid") String userId,
                                                      @RequestBody Map<String, Object[]> areaOfWorkMap) {
        areaOfWorkService.upsertMultipleAreaOfWork(rootOrg, userId, areaOfWorkMap);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/v3/users/{userid}/getAllAreaOfWork")
    public ResponseEntity<Map<String, Object>> getAreaOfWork(@RequestHeader(value = "rootOrg") String rootOrg,
                                                             @NotNull @PathVariable("userid") String userId) {

        Map<String, Object> userAreasOfWork = new HashMap<String, Object>();
        userAreasOfWork = areaOfWorkService.getAreaOfWork(rootOrg, userId);
        return new ResponseEntity<Map<String, Object>>(userAreasOfWork, HttpStatus.OK);
    }

    @DeleteMapping("/v3/users/{userid}/deleteAreaOfWork")
    public ResponseEntity<String> deleteAreaOfWork(@RequestHeader(value = "rootOrg") String rootOrg,
                                                   @PathVariable("userid") String userId, @RequestBody Map<String, Object> areaOfWorkMap) {
        areaOfWorkService.deleteAreaOfWork(rootOrg, userId, areaOfWorkMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
