package com.nlpcaptcha.captcha.controller;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.*;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import com.nlpcaptcha.captcha.services.ListChallengeService;
import com.nlpcaptcha.captcha.services.PairChallengeService;
import com.nlpcaptcha.captcha.services.PairService;
import com.nlpcaptcha.captcha.services.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/datareader")
public class DataReaderController {

    @Autowired
    ListChallengeRepository listChallengeRepository;

    @Autowired
    PairChallengeRepository pairChallengeRepository;

    @Autowired
    UsagePairRepository usagePairRepository;

    @Autowired
    UsageRepository usageRepository;

    @Autowired
    ListChallengeService listChallengeService;

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @PostMapping("/usage-pairs")
    public ResponseEntity<String> addUsagePairsFromFile(@RequestBody String path) {
        List<UsagePair> usagePairs = null;
        try {
            PairService pairService = new PairService(usageRepository, usagePairRepository);
            usagePairs = pairService.readAndSavePairs(path);

        } catch (Exception e) {
            logger.error("An error occurred while reading Pairs: ", e);
            return new ResponseEntity<>("Error while reading and creating Data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Data read and created successfully: \n" + usagePairs, HttpStatus.OK);


    }

    @PostMapping("/usages")
    public ResponseEntity<String> addUsagesFromFile(@RequestBody String path) {

        try {
            UsageService usageService = new UsageService(usageRepository);
            List<Usage> usages = usageService.readData(path);
            usageRepository.saveAll(usages);
        } catch (Exception e) {
            logger.error("An error occurred while reading Usages: ", e);
            return new ResponseEntity<>("Error while reading and creating Data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Data read and created successfully", HttpStatus.OK);


    }

    @PostMapping("/pair-challenges")
    public ResponseEntity<String> addPairChallengesFromFile(@RequestBody String path) {

        try {
            PairChallengeService pairChallengeService = new PairChallengeService(pairChallengeRepository, usagePairRepository, usageRepository);
            List<PairChallenge> pairChallenges = pairChallengeService.readData(path);
            pairChallengeRepository.saveAll(pairChallenges);
        } catch (Exception e) {
            logger.error("An error occurred while reading Pair Challenges: ", e);
            return new ResponseEntity<>("Error while reading and creating Data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Pair Challenges read and created successfully", HttpStatus.OK);


    }

    @PostMapping("/list-challenges")
    public ResponseEntity<String> addListChallengesFromFile(@RequestBody String path) {
        List<ListRankingChallenge> listChallenges;
        try {

            listChallenges = listChallengeService.readData(path);

        } catch (Exception e) {
            logger.error("An error occurred while reading List Challenges: ", e);
            return new ResponseEntity<>("Error while reading and creating Data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("List Challenges read and created successfully: \n" + listChallenges, HttpStatus.OK);
    }
}
