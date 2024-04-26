package com.nlpcaptcha.captcha.controller;

import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PairChallengeController {

    @Autowired
    PairChallengeRepository pairChallengeRepository;

    @GetMapping("/pairchallenges")
    public List<PairChallenge> getAllPairChallenges() {


        return pairChallengeRepository.findAll();
    }
}
