package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.model.StudyUserData;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import com.nlpcaptcha.captcha.repository.StudyCombinedChallengeRepository;
import com.nlpcaptcha.captcha.repository.StudyUserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudyService {

    private final StudyCombinedChallengeRepository studyCombinedChallengeRepository;
    private final PairChallengeService pairChallengeService;
    private final ListChallengeService listRankingChallengeService;
    private final ListChallengeRepository listChallengeRepository;
    private final PairChallengeRepository pairChallengeRepository;
    private final StudyUserDataRepository studyUserDataRepository;


    @Autowired
    public StudyService(StudyCombinedChallengeRepository studyCombinedChallengeRepository, PairChallengeService pairChallengeService, ListChallengeService listRankingChallengeService, ListChallengeRepository listChallengeRepository, PairChallengeRepository pairChallengeRepository, StudyUserDataRepository studyUserDataRepository) {
        this.studyCombinedChallengeRepository = studyCombinedChallengeRepository;
        this.pairChallengeService = pairChallengeService;
        this.listRankingChallengeService = listRankingChallengeService;
        this.listChallengeRepository = listChallengeRepository;
        this.pairChallengeRepository = pairChallengeRepository;
        this.studyUserDataRepository = studyUserDataRepository;
    }

    @Transactional
    public List<StudyCombinedChallenge> readData(String path) throws JSONException {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);

        List<String> header = records.removeFirst();//remove header


        int identifierIndex = header.indexOf("identifier");

        int pairChallengeIndex = header.indexOf("pair_challenge_identifier");
        int listRankingChallengeIndex = header.indexOf("list_challenge_identifier");

        List<StudyCombinedChallenge> studyCombinedChallenges = new ArrayList<>();

        for (List<String> record : records) {

            StudyCombinedChallenge studyCombinedChallenge = studyCombinedChallengeRepository.findByIdentifier(record.get(identifierIndex));
            if (studyCombinedChallenge != null) {
                studyCombinedChallenges.add(studyCombinedChallenge);
                continue;
            }


            String pairChallengeIdentifier = record.get(pairChallengeIndex);
            String listRankingChallengeIdentifier = record.get(listRankingChallengeIndex);

            studyCombinedChallenge = createAndSaveStudyCombinedChallengeByIdentifier(pairChallengeIdentifier, listRankingChallengeIdentifier);

            studyCombinedChallenges.add(studyCombinedChallenge);
        }

        return studyCombinedChallenges;
    }


    @Transactional
    public StudyCombinedChallenge createAndSaveStudyCombinedChallengeByIdentifier(String pairChallengeIdentifier, String listRankingChallengeIdentifier) throws JSONException {

        String identifier = pairChallengeIdentifier + "|||" + listRankingChallengeIdentifier;

        StudyCombinedChallenge studyCombinedChallenge = studyCombinedChallengeRepository.findByIdentifier(identifier);

        if (studyCombinedChallenge != null) {
            return studyCombinedChallenge;
        }

        PairChallenge pairChallenge = pairChallengeService.createAndSavePairChallengeByIdentifier(pairChallengeIdentifier);
        ListRankingChallenge listChallenge = listChallengeRepository.findByIdentifier(listRankingChallengeIdentifier);

        if (pairChallenge == null || listChallenge == null) {
            throw new IllegalArgumentException(" One or both of the challenges not found  ");
        }

        studyCombinedChallenge = studyCombinedChallengeRepository.save(new StudyCombinedChallenge(pairChallenge, listChallenge));

        pairChallengeRepository.save(pairChallenge);
        listChallengeRepository.save(listChallenge);

        return studyCombinedChallenge;
    }

    public StudyCombinedChallenge saveStudyUserData(String studyUserDataString) {


        try {
            JSONObject jsonUSerData = new JSONObject(studyUserDataString);

            Long studyChallengeId = jsonUSerData.getLong("studyChallengeId");

            Optional<StudyCombinedChallenge> studyCombinedChallenge = studyCombinedChallengeRepository.findById(studyChallengeId);

            if (studyCombinedChallenge.isEmpty()) {
                throw new IllegalArgumentException("Study Challenge not found");
            }


            String pairChallengeResults = jsonUSerData.getString("pairChallengeResults");
            String listChallengeResults = jsonUSerData.getString("listChallengeResults");
            long startTime = jsonUSerData.getLong("startTime");
            long endTimePairChallenge = jsonUSerData.getLong("endTimePairChallenge");
            long endTime = jsonUSerData.getLong("endTime");
            String userFeedback = jsonUSerData.getString("userFeedback");


            StudyUserData studyUserData = new StudyUserData(studyCombinedChallenge.get(), pairChallengeResults,
                    listChallengeResults, startTime, endTimePairChallenge, endTime, userFeedback);


            studyUserDataRepository.save(studyUserData);

            return studyCombinedChallengeRepository.save(studyCombinedChallenge.get());


        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

    }
}