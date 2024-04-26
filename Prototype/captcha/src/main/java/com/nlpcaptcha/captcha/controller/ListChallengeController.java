package com.nlpcaptcha.captcha.controller;


import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ListChallengeController {

    @Autowired
    ListChallengeRepository listChallengeRepository;

    @GetMapping("/listchallenges")
    public List<ListRankingChallenge> getAllListChallenges() {
        //ResponseEntity(List<ListChallenge>)


        return listChallengeRepository.findAll();
    }



    @PostMapping("/addlistchallenge")
    public ResponseEntity<ListRankingChallenge> saveListChallenge(@RequestBody ListRankingChallenge listRankingChallenge) {
        try {
            ListRankingChallenge _listRankingChallenge = listChallengeRepository
                    .save(new ListRankingChallenge(listRankingChallenge.getLemma(), listRankingChallenge.getReferenceUsage(), listRankingChallenge.getListUsages()));

            return new ResponseEntity<>(_listRankingChallenge, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
