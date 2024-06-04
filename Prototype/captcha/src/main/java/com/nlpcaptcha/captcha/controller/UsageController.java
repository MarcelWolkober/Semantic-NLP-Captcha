package com.nlpcaptcha.captcha.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/usages")
public class UsageController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    @Autowired
    UsageRepository usageRepository;



    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<Usage> getAllUsagePairs() {
        return usageRepository.findAll();
    }

    @PostMapping("/find")
    public ResponseEntity<Usage> findByIdentifier(@RequestBody String identifier) {

        try {
            Usage usage = usageRepository.findByIdentifier(identifier);
            return new ResponseEntity<>(usage, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
