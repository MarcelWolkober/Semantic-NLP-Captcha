package com.nlpcaptcha.captcha.controller;

import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsagePairController {
    @Autowired
    UsagePairRepository usagePairRepository;

    @Autowired
    UsageRepository usageRepository;

    @GetMapping("/usagepairs")
    public List<UsagePair> getAllUsagePairs() {


        return usagePairRepository.findAll();
    }
}
