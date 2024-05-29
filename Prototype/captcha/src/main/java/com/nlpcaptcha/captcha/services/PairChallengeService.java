package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PairChallengeService {


    private final PairChallengeRepository pairChallengeRepository;
    private final UsagePairRepository usagePairRepository;

    private final PairService pairService;

    @Autowired
    public PairChallengeService(PairChallengeRepository pairChallengeRepository, UsagePairRepository usagePairRepository, UsageRepository usageRepository) {
        this.pairChallengeRepository = pairChallengeRepository;
        this.usagePairRepository = usagePairRepository;
        this.pairService = new PairService(usageRepository, usagePairRepository);
    }

    @Transactional
    public List<PairChallenge> readData(String path) throws JSONException {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header    }

        List<PairChallenge> pairChallenges = new ArrayList<>();

        for (List<String> record : records) {


            String identifier = record.getFirst();

            if (pairChallengeRepository.existsByIdentifier(identifier)) {
                continue;
            }

            PairChallenge pairChallenge = createAndSavePairChallenge(identifier, record);

            pairChallenges.add(pairChallenge);
        }

        return pairChallenges;
    }

    @Transactional
    public PairChallenge createAndSavePairChallenge(String identifier, List<String> record) throws JSONException {

        if (pairChallengeRepository.existsByIdentifier(identifier)) {
            return pairChallengeRepository.findByIdentifier(identifier);
        }

        //split identifier into pair identifiers
        String[] pairIdentifiers = identifier.split("\\|\\|");
        Set<UsagePair> pairs = new HashSet<>();


        //check if pair identifier at last position is empty and remove from array
        if (Objects.equals(pairIdentifiers[pairIdentifiers.length - 1], "")) {
            String[] temp = new String[pairIdentifiers.length - 1];
            System.arraycopy(pairIdentifiers, 0, temp, 0, pairIdentifiers.length - 1);
            pairIdentifiers = temp;
        }

        for (int i = 0; i < pairIdentifiers.length; i++) {
            UsagePair pair = usagePairRepository.findByIdentifier(pairIdentifiers[i]);

            if (pair == null) {

                JSONObject stringPair = new JSONObject(record.get(i + 1));

                pair = pairService.createAndSavePairByUsageIdentifiers(stringPair.getString("identifier1"),
                        stringPair.getString("identifier2"),
                        Float.parseFloat(stringPair.getString("judgment")));
            }
            usagePairRepository.save(pair);
            pairs.add(pair);
        }

        return pairChallengeRepository.save(new PairChallenge(identifier, pairs));
    }


    @Transactional
    public void deletePairChallenge(PairChallenge pairChallenge) {

        Set<UsagePair> pairs = pairChallenge.getUsagePairs();

        for (UsagePair pair : pairs) {
            if (pair.getPairChallenges().contains(pairChallenge)) {
                pair.removePairChallenge(pairChallenge);
                usagePairRepository.save(pair);
            }
        }
        pairChallengeRepository.delete(pairChallenge);
    }

}
