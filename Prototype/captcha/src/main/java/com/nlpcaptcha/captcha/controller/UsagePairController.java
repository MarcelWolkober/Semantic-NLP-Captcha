package com.nlpcaptcha.captcha.controller;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")//TODO for testing only
@RestController
@RequestMapping("/api/usagepairs")
public class UsagePairController {
    @Autowired
    UsagePairRepository usagePairRepository;

    @Autowired
    UsageRepository usageRepository;

    @GetMapping("/all")
    public List<UsagePair> getAllUsagePairs() {
        return usagePairRepository.findAll();
    }

    @GetMapping("/")
    public ResponseEntity<UsagePair> getFirstUsagePair() {
        try {
            UsagePair pair = usagePairRepository.findAll().getFirst();
            return new ResponseEntity<>(pair, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     @GetMapping("/next")
    public ResponseEntity<UsagePair> getNextUsagePair() {
        try {
            UsagePair pair = usagePairRepository.findAll().getFirst(); //TODO adjust to get next
            return new ResponseEntity<>(pair, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
