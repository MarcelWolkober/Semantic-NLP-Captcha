package com.nlpcaptcha.captcha.controller;

import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.*;
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

    @PostMapping("/usagepairs")
    public ResponseEntity<String> addUsagePairsFromFile(@RequestBody String path) {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();

        try {
            for (List<String> record : records) {

                int startIndex1 = Integer.parseInt(record.get(5).split(":")[0]);
                int endIndex1 = Integer.parseInt(record.get(5).split(":")[1]);
                Usage usage1 = new Usage(record.get(0), record.get(3), startIndex1, endIndex1);
                //usageRepository.save(usage1);

                int startIndex2 = Integer.parseInt(record.get(6).split(":")[0]);
                int endIndex2 = Integer.parseInt(record.get(6).split(":")[1]);
                Usage usage2 = new Usage(record.get(0), record.get(4), startIndex2, endIndex2);
                //usageRepository.save(usage2);

                float label = Float.parseFloat(record.get(7));
                UsagePair pair = new UsagePair(usage1, usage2, label);
                usagePairRepository.save(pair);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error while reading and creating Data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Data read and created successfully", HttpStatus.OK);


    }


}
