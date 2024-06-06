package com.nlpcaptcha.captcha.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.model.Views;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import com.nlpcaptcha.captcha.services.PairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")//TODO for testing only
@RestController
@RequestMapping("/api/usage-pairs")
public class UsagePairController {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);


    @Autowired
    UsagePairRepository usagePairRepository;

    @Autowired
    UsageRepository usageRepository;
    @Autowired
    private PairService pairService;


    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<UsagePair> getAllUsagePairs() {

        return usagePairRepository.findAll();
    }

    @GetMapping("/")
    public ResponseEntity<UsagePair> getFirstUsagePair() {
        try {
            UsagePair pair = usagePairRepository.findAll().getFirst();
            return new ResponseEntity<>(pair, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("An error occurred while getting the first pair: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/next")
    public ResponseEntity<UsagePair> getNextUsagePair() {
        try {
            UsagePair pair = usagePairRepository.findAll().getFirst(); //TODO adjust to get next
            return new ResponseEntity<>(pair, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while getting the next pair: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/remove-all")
    public ResponseEntity<String> removeAllUsagePairs() {
        try {

            List<UsagePair> pairs = usagePairRepository.findAll();

            for (UsagePair usagePair : pairs) {
               pairService.deletePair(usagePair);
            }
            return new ResponseEntity<>("Deleted all usage pairs successfully",HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while deleting all pairs: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
