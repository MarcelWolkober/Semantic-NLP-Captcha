package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import com.nlpcaptcha.captcha.model.PairChallenge;
import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.model.StudyUserData;
import com.nlpcaptcha.captcha.repository.ListChallengeRepository;
import com.nlpcaptcha.captcha.repository.PairChallengeRepository;
import com.nlpcaptcha.captcha.repository.StudyCombinedChallengeRepository;
import com.nlpcaptcha.captcha.repository.StudyUserDataRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class StudyService {

    private static final Logger logger = LoggerFactory.getLogger(StudyService.class);

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
    public StudyCombinedChallenge createNewRandomStudyChallenge() throws IllegalStateException {


        List<PairChallenge> pairChallenges = pairChallengeRepository.findAll();
        List<ListRankingChallenge> listRankingChallenges =listChallengeRepository.findAll();

        if (pairChallenges.isEmpty() || listRankingChallenges.isEmpty()) {
            throw new IllegalStateException("No available PairChallenges or ListRankingChallenges");
        }


        // filter PairChallenges and ListRankingChallenges by smallest number of associated StudyChallenges
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            // Fetch all PairChallenges and ListRankingChallenges that do not have any associated StudyUserData
            int numberOfStudyChallenges = i;
            pairChallenges = pairChallengeRepository.findAll().stream()
                    .filter(pairChallenge -> pairChallenge.getStudyCombinedChallenges().size() == numberOfStudyChallenges).toList();
            listRankingChallenges = listChallengeRepository.findAll().stream()
                    .filter(listRankingChallenge -> listRankingChallenge.getStudyCombinedChallenges().size() == numberOfStudyChallenges).toList();

            if (!pairChallenges.isEmpty() && !listRankingChallenges.isEmpty()) {
                break;

            }
        }
        // Check if there are any available PairChallenges and ListRankingChallenges -> should not do anything with the loop before going till Integer.MAX_VALUE
        if (pairChallenges.isEmpty() || listRankingChallenges.isEmpty()) {
            throw new IllegalStateException("No available PairChallenges or ListRankingChallenges");
        }

        // Randomly select one PairChallenge and one ListRankingChallenge
        Random random = new Random();
        PairChallenge randomPairChallenge = pairChallenges.get(random.nextInt(pairChallenges.size()));
        ListRankingChallenge randomListRankingChallenge = listRankingChallenges.get(random.nextInt(listRankingChallenges.size()));

        // Create a new StudyCombinedChallenge using the selected PairChallenge and ListRankingChallenge
        return createAndSaveStudyCombinedChallengeByIdentifier(randomPairChallenge.getIdentifier(), randomListRankingChallenge.getIdentifier());

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

        logger.info("Saving StudyUserData: {}", studyUserDataString);
        try {
            JSONObject jsonUSerData = new JSONObject(studyUserDataString);

            Long studyChallengeId = jsonUSerData.getLong("studyChallengeId");

            Optional<StudyCombinedChallenge> studyCombinedChallenge = studyCombinedChallengeRepository.findById(studyChallengeId);

            if (studyCombinedChallenge.isEmpty()) {
                throw new IllegalArgumentException("Study Challenge not found");
            }


            String pairChallengeResults = jsonUSerData.getJSONObject("pairChallengeResults").toString();
            String listChallengeResults = jsonUSerData.getJSONObject("listChallengeResults").toString();
            long startTime = jsonUSerData.getLong("startTime");
            long endTimeFirstChallenge = jsonUSerData.getLong("endTimeFirstChallenge");
            long endTime = jsonUSerData.getLong("endTime");
            String userFeedback = jsonUSerData.getJSONObject("userFeedback").toString();


            StudyUserData studyUserData = new StudyUserData(studyCombinedChallenge.get(), pairChallengeResults,
                    listChallengeResults, startTime, endTimeFirstChallenge, endTime, userFeedback);

            logger.info("Saving StudyUserData: {}", studyUserData);

            StudyUserData studyUserDataSave = studyUserDataRepository.save(studyUserData);

            logger.info("Saved StudyUserData: {}", studyUserDataSave);

            return studyCombinedChallengeRepository.save(studyCombinedChallenge.get());


        } catch (Exception e) {
            logger.error("error while saving StudyUserData: ", e);
            throw new IllegalArgumentException("Invalid JSON format");
        }

    }
}
