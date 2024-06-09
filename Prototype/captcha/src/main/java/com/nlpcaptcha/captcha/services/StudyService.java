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
import org.springframework.scheduling.annotation.Async;
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
    public StudyCombinedChallenge getRandomStudyChallenge() {
        List<StudyCombinedChallenge> studies = studyCombinedChallengeRepository.findAll();

        int minAssignedUserData = studies.stream()
                .mapToInt(study -> study.getStudyUserData().size())
                .min()
                .orElseThrow(() -> new IllegalStateException("No studies found"));

        studies = studies.stream()
                .filter(study -> study.getStudyUserData().size() == minAssignedUserData)
                .toList();

        Random random = new Random();
        int randomIndex = random.nextInt(studies.size());
        return studies.get(randomIndex);
    }

    @Transactional
    public StudyCombinedChallenge getNextAvailableStudyChallenge(int sizeOfStudyChallengePool, int numberOfStudyUsersPerChallenge) {
        List<StudyCombinedChallenge> studies = studyCombinedChallengeRepository.findAll();

        hasMoreThanXStudyChallenges(sizeOfStudyChallengePool, sizeOfStudyChallengePool, numberOfStudyUsersPerChallenge);

        int minAssignedUserData = studies.stream()
                .mapToInt(study -> study.getStudyUserData().size())
                .min()
                .orElseThrow(() -> new IllegalStateException("No studies found"));

        studies = studies.stream()
                .filter(study -> study.getStudyUserData().size() == minAssignedUserData)
                .toList();

        Random random = new Random();
        int randomIndex = random.nextInt(studies.size());

        return studies.get(randomIndex);
    }

    @Async
    public void hasMoreThanXStudyChallenges(int xNumberOfChallengesToHave, int numberOfChallengesToCreate, int maxNumberOfAssignedUserData) {
        List<StudyCombinedChallenge> challenges = studyCombinedChallengeRepository.findAll();

        challenges = challenges.stream()
                .filter(challenge -> challenge.getStudyUserData().size() < maxNumberOfAssignedUserData)
                .toList();

        logger.info("Number of study challenges: {}", challenges.size());
        if (challenges.size() < xNumberOfChallengesToHave) {
            createMultipleNewRandomStudyChallenges(numberOfChallengesToCreate);
            logger.info("Creating {} new study challenges", numberOfChallengesToCreate);
        }
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
    public void createMultipleNewRandomStudyChallenges(int numberOfChallenges) {

        try {
            for (int i = 0; i < numberOfChallenges; i++) {
                createNewRandomStudyChallenge();
            }
        } catch (Exception e) {
            logger.error("Error while creating multiple new random study challenges: ", e);
        }

    }

    @Transactional
    public StudyCombinedChallenge createNewRandomStudyChallenge() throws IllegalStateException {


        PairChallenge randomPairChallenge = pairChallengeService.createNewRandomPairChallengeForStudy();
        ListRankingChallenge randomListRankingChallenge = listRankingChallengeService.getNewRandomListChallenge(4);

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


            String pairChallengeResults = "";
            String listChallengeResults = "";
            String userFeedback = "";
            long startTime = jsonUSerData.getLong("startTime");

            long endTimeFirstChallenge = 0 ;
            long endTime = jsonUSerData.getLong("endTime");


            try {
                pairChallengeResults = jsonUSerData.getJSONObject("pairChallengeResults").toString();

            } catch (JSONException ignored) {
            }

            try {
                listChallengeResults = jsonUSerData.getJSONObject("listChallengeResults").toString();

            } catch (JSONException ignored) {
            }

            try {
                userFeedback = jsonUSerData.getJSONObject("userFeedback").toString();

            } catch (JSONException ignored) {
            }

            try {
               endTimeFirstChallenge = jsonUSerData.getLong("endTimeFirstChallenge");
            }catch (JSONException ignored) {
            }


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
