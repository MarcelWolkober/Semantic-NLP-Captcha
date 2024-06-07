package com.nlpcaptcha.captcha.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.StudyCombinedChallengeRepository;
import com.nlpcaptcha.captcha.services.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://front-end:8080")//TODO find a better way to allow only localhost
@RestController
@RequestMapping("/api/study")
public class StudyController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyCombinedChallengeRepository studyCombinedChallengeRepository;


    @GetMapping("/new")
    @JsonView(Views.Public.class)
    public ResponseEntity<StudyCombinedChallenge> getNewStudyChallenge() {

        try {
            StudyCombinedChallenge studyChallenge = studyService.getNextAvailableStudyChallenge();
            return new ResponseEntity<>(studyChallenge, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while creating new study: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<StudyCombinedChallenge> getAllPairChallenges() {
        return studyCombinedChallengeRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<StudyCombinedChallenge> addStudyUserData(@RequestBody String studyUserDataString) {
        try {
            studyService.hasMoreThanFiveStudyChallenges();
            StudyCombinedChallenge studyUserData = studyService.saveStudyUserData(studyUserDataString);
            logger.info("Study user data added successfully");
            return new ResponseEntity<>(studyUserData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while adding study user data: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
