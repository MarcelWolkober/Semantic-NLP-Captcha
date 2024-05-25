package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;

import java.util.ArrayList;
import java.util.List;

public class ListChallengeService {

    public List<ListRankingChallenge> readData(String path) {
       DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header

        List<ListRankingChallenge> listChallenges = new ArrayList<>();

        for (List<String> record : records) {
            //TODO: map record to ListRankingChallenge by searching for the Usages
        }
        return listChallenges;
    }

}
