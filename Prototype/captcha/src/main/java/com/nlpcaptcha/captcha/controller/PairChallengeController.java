package com.nlpcaptcha.captcha.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import com.nlpcaptcha.captcha.services.PairChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pair-challenges")
public class PairChallengeController {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @Autowired
    PairChallengeRepository pairChallengeRepository;
    @Autowired
    private PairChallengeService pairChallengeService;

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<PairChallenge> getAllPairChallenges() {

        List<PairChallenge> pairChallenges = pairChallengeRepository.findAll();
        return pairChallenges;
    }

    @GetMapping("/remove-all")
    public ResponseEntity<String> removeAllPairChallenges() {

        try {
            List<PairChallenge> challenges = pairChallengeRepository.findAll();

            for (PairChallenge usagePair : challenges) {
                pairChallengeService.deletePairChallenge(usagePair);
            }
            return new ResponseEntity<>("Deleted all pair challenges successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while deleting all pairs: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


}
