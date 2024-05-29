package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ListChallengeService {

    @Autowired
    private ListChallengeRepository listChallengeRepository;

    @Autowired
    private UsageService usagePairService;

    @Transactional
    public List<ListRankingChallenge> readData(String path) {
       DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header

        List<ListRankingChallenge> listChallenges = new ArrayList<>();

        for (List<String> record : records) { // Record =  lemma	identifier_ref	identifier1	judgment1	identifier2	judgment2	identifier3	judgment3	identifier4	judgment4	order	to_find	count
            //TODO: map record to ListRankingChallenge by searching for the Usages



        }
        return listChallenges;
    }

    @Transactional
    public ListRankingChallenge createAndSaveListChallenge(String lemma, List<String> identifier, List<Float> labels) {

        //TODO: create usagePairs and new ListRankingChallenge


        ListRankingChallenge listChallenge = new ListRankingChallenge(lemma, usagePairService.getUsageByIdentifier(identifier), new ArrayList<>());
        for (int i = 2; i < record.size(); i += 2) {
            listChallenge.addListUsage(usagePairService.getUsageByIdentifier(record.get(i)));
        }
        return listChallengeRepository.save(listChallenge);
    }

}
