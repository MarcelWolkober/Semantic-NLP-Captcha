package com.nlpcaptcha.captcha.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list-challenges")
public class ListChallengeController {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @Autowired
    ListChallengeRepository listChallengeRepository;

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<ListRankingChallenge> getAllListChallenges() {
        //ResponseEntity(List<ListChallenge>)


        return listChallengeRepository.findAll();
    }


    @PostMapping("/add")
    public ResponseEntity<ListRankingChallenge> saveListChallenge(@RequestBody ListRankingChallenge listRankingChallenge) {
        try {
            ListRankingChallenge _listRankingChallenge = listChallengeRepository
                    .save(new ListRankingChallenge(listRankingChallenge.getLemma(), listRankingChallenge.getReferenceUsage(), listRankingChallenge.getListUsages()));

            return new ResponseEntity<>(_listRankingChallenge, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("An error occurred while saving list challenge: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
