package com.nlpcaptcha.captcha.controller;


import com.nlpcaptcha.captcha.model.ListChallenge;
import com.nlpcaptcha.captcha.model.Position;
import com.nlpcaptcha.captcha.model.Usage;
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
    public List<ListChallenge> getAllListChallenges() {
        //ResponseEntity(List<ListChallenge>)
        return listChallengeRepository.findAll();
    }



    @PostMapping("/addlistchallenge")
    public ResponseEntity<ListChallenge> saveListChallenge(@RequestBody ListChallenge listChallenge) {
        try {
            ListChallenge _listChallenge = listChallengeRepository
                    .save(new ListChallenge(listChallenge.getLemma(), listChallenge.getReferenceUsage(), listChallenge.getListUsages()));

            return new ResponseEntity<>(_listChallenge, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
