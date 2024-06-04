package com.nlpcaptcha.captcha;

import com.nlpcaptcha.captcha.controller.UsageController;
import com.nlpcaptcha.captcha.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    private final UsageService usageService;
    private final PairService pairService;
    private final PairChallengeService pairChallengeService;
    private final ListChallengeService listChallengeService;
    private final StudyService studyService;

    @Autowired
    public AppStartupRunner(UsageService usageService, PairService pairService, PairChallengeService pairChallengeService, ListChallengeService listChallengeService, StudyService studyService) {
        this.usageService = usageService;
        this.pairService = pairService;
        this.pairChallengeService = pairChallengeService;
        this.listChallengeService = listChallengeService;
        this.studyService = studyService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.atInfo().log("Application started with command-line arguments: {}", args.getOptionNames());
        try {
            logger.atInfo().log("Setting up the data");
            usageService.readData("dwug_en_usages.csv");
            logger.atInfo().log("usages read");
            pairService.readAndSavePairs("dwug_en_pairs.csv");
            logger.atInfo().log("pairs read");
            pairChallengeService.readData("dwug_en_pair_challenges_3.csv", true);
            logger.atInfo().log("pair challenges read");
            listChallengeService.readData("dwug_en_list_challenges_filtered.csv");
            logger.atInfo().log("list challenges read");
        } catch (Exception e) {
            logger.error("An error occurred while setting up the data: ", e);
        }

    }


}