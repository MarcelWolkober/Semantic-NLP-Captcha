package com.nlpcaptcha.captcha.services;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PairChallengeService {


    private final PairChallengeRepository pairChallengeRepository;
    private final UsagePairRepository usagePairRepository;

    private final PairService pairService;

    @Autowired
    public PairChallengeService(PairChallengeRepository pairChallengeRepository, UsagePairRepository usagePairRepository, UsageRepository usageRepository) {
        this.pairChallengeRepository = pairChallengeRepository;
        this.usagePairRepository = usagePairRepository;
        this.pairService = new PairService(usageRepository);
    }


    public List<PairChallenge> readData(String path) throws JSONException {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header    }

        List<PairChallenge> pairChallenges = new ArrayList<>();

        for (List<String> record : records) {

            List<UsagePair> pairs = new ArrayList<>();

            String identifier = record.getFirst();
            String[] identifiers = identifier.split("\\|\\|");

            if (Objects.equals(identifiers[identifiers.length - 1], "")) {
                String[] temp = new String[identifiers.length - 1];
                System.arraycopy(identifiers, 0, temp, 0, identifiers.length - 1);
                identifiers = temp;
            }

            for (int i = 0; i < identifiers.length; i++) {
                UsagePair pair = usagePairRepository.findByIdentifier(identifiers[i]);

                if (pair == null) {

                    JSONObject stringPair = new JSONObject(record.get(i + 1));

                    pair = pairService.createPairByUsageIdentifiers(stringPair.getString("identifier1"),
                            stringPair.getString("identifier2"),
                            Float.parseFloat(stringPair.getString("judgment")));

                }

                pairs.add(pair);


            }
            pairChallenges.add(new PairChallenge(identifier, pairs));
        }

        return pairChallenges;
    }


}
