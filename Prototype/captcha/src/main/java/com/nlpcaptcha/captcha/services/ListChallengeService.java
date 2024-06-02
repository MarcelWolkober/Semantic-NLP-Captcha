package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Configurable
@Transactional
public class ListChallengeService {


    private final ListChallengeRepository listChallengeRepository;
    private final UsageRepository usageRepository;
    private final PairService pairService;

    @Autowired
    public ListChallengeService(ListChallengeRepository listChallengeRepository, UsageRepository usageRepository, PairService pairService) {
        this.listChallengeRepository = listChallengeRepository;
        this.usageRepository = usageRepository;
        this.pairService = pairService;
    }

    @Transactional
    public List<ListRankingChallenge> readData(String path) throws JSONException {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);

        List<String> header = records.removeFirst();//remove header

        int orderIndex = header.indexOf("order");

        List<ListRankingChallenge> listChallenges = new ArrayList<>();

        for (List<String> record : records) { // Record =  lemma	identifier_ref	identifier1	judgment1	identifier2	judgment2	identifier3	judgment3	identifier4	judgment4	order	to_find	count
            //TODO: map record to ListRankingChallenge by searching for the Usages
            String lemma = record.get(0);
            String referenceUsageIdentifier = record.get(1);
            List<String> listUsagesIdentifiers = new ArrayList<>();
            List<Float> labels = new ArrayList<>();

            String orderString = record.get(orderIndex);
            JSONArray jsonArray = new JSONArray(orderString);

            List<String> order = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                order.add(jsonArray.getString(i));
            }

            for (int i = 2; i < orderIndex; i += 2) {

                String tempIdentifier = record.get(i);
                String tempLabel = record.get(i + 1);

                if (tempIdentifier.isBlank() || tempLabel.isBlank()) {
                    continue;
                }

                listUsagesIdentifiers.add(tempIdentifier);
                labels.add(Float.parseFloat(tempLabel));
            }

            listChallenges.add(createAndSaveListChallengeByIdentifiers(referenceUsageIdentifier, listUsagesIdentifiers, labels, order));

        }
        return listChallenges;
    }

    @Transactional
    public ListRankingChallenge createAndSaveListChallengeByIdentifiers(String referenceUsageIdentifier, List<String> listUsagesIdentifiers, List<Float> labels, List<String> order) throws IllegalArgumentException {

        String challengeIdentifier = referenceUsageIdentifier + "||" + String.join("||", listUsagesIdentifiers);

        ListRankingChallenge listChallenge = listChallengeRepository.findByIdentifier(challengeIdentifier);

        if (listChallenge != null) {
            return listChallenge;
        } else if (listUsagesIdentifiers.size() != labels.size()) {
            throw new IllegalArgumentException("The number of labels must be equal to the number of list usages");
        }

        List<Usage> listUsages = new ArrayList<>();
        Usage referenceUsage = usageRepository.findByIdentifier(referenceUsageIdentifier);
        String lemma = referenceUsage.getLemma();

        for (String identifier : listUsagesIdentifiers) {
            Usage tempUsage = usageRepository.findByIdentifier(identifier);
            String tempIdentifier = referenceUsageIdentifier + "|" + identifier;


            pairService.createAndSavePairByUsageIdentifiers(referenceUsageIdentifier, identifier, labels.get(listUsagesIdentifiers.indexOf(identifier)));

            listUsages.add(tempUsage);

        }

        listChallenge = new ListRankingChallenge(challengeIdentifier, lemma, referenceUsage, listUsages, order);

        usageRepository.save(referenceUsage);
        usageRepository.saveAll(listUsages);

        return listChallengeRepository.save(listChallenge);
    }


}
