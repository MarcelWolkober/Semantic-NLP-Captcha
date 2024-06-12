package com.nlpcaptcha.captcha.controller;


import com.nlpcaptcha.captcha.model.PairUserChoice;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resolve/")
public class ChallengeResolverController {


     @Autowired
     UsagePairRepository usagePairRepository;

    @Autowired
    UsageRepository usageRepository;

    @PostMapping("/pair")
    public boolean resolvePairChallenge(@RequestBody PairUserChoice data) {

        UsagePair pair = usagePairRepository.getReferenceById(data.id);

        return pair.getLabel() == data.choice;


    }

}
