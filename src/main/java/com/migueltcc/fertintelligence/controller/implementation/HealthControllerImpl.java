package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.HealthController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/health")
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class HealthControllerImpl implements HealthController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }
}