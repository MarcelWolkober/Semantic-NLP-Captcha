package com.nlpcaptcha.captcha.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.model.StudyUserData;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.StudyCombinedChallengeRepository;
import com.nlpcaptcha.captcha.services.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")//TODO for testing only
@RestController
@RequestMapping("/api/study")
public class StudyController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyCombinedChallengeRepository studyCombinedChallengeRepository;


    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<StudyCombinedChallenge> getAllPairChallenges() {

        List<StudyCombinedChallenge> studyCombinedChallenges = studyCombinedChallengeRepository.findAll();
        return studyCombinedChallenges;
    }

    @PostMapping("/add")
    public ResponseEntity<StudyUserData> addStudyUserData(@RequestBody StudyUserData studyUserData) {
        try {
            studyService.saveStudyUserData(studyUserData);
            return new ResponseEntity<>(studyUserData, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("An error occurred while adding study user data: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
