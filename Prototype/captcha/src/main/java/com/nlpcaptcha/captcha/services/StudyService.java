package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.*;
import com.nlpcaptcha.captcha.repository.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final UsageRepository usageRepository;


    @Autowired
    public StudyService(StudyCombinedChallengeRepository studyCombinedChallengeRepository, PairChallengeService pairChallengeService, ListChallengeService listRankingChallengeService, ListChallengeRepository listChallengeRepository, PairChallengeRepository pairChallengeRepository, StudyUserDataRepository studyUserDataRepository, UsageRepository usageRepository) {
        this.studyCombinedChallengeRepository = studyCombinedChallengeRepository;
        this.pairChallengeService = pairChallengeService;
        this.listRankingChallengeService = listRankingChallengeService;
        this.listChallengeRepository = listChallengeRepository;
        this.pairChallengeRepository = pairChallengeRepository;
        this.studyUserDataRepository = studyUserDataRepository;
        this.usageRepository = usageRepository;
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

            long endTimeFirstChallenge = 0;
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
            } catch (JSONException ignored) {
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

    public void writeStudyUserData() {

        List<Usage> allUsages = usageRepository.findAll();

        List<StudyCombinedChallenge> studyCombinedChallenges = studyCombinedChallengeRepository.findAll();

        String[] studyChallengeHeader = new String[]{"study_id", "lemma", "pair_challenge_string_identifier", "list_challenge_string_identifier"};
        List<String[]> studyChallenge = new ArrayList<>();

        String[] studyResultsHeader = new String[]{"study_id", "time_to_complete", "p_c_results_id", "l_c_results_id", "feedback_id"};
        List<String[]> studyResults = new ArrayList<>();

        String[] pairChallengeResultsHeader = new String[]{"p_c_results_id", "pair1_results", "pair2_results", "pair3_results", "pair4_results",
                "pair5_results", "pair6_results"};
        List<String[]> pairChallengeResults = new ArrayList<>();

        String[] listChallengeResultsHeader = new String[]{"l_c_results_id", "chosen_order", "string_identifier"};
        List<String[]> listChallengeResults = new ArrayList<>();

        String[] feedbackHeader = new String[]{"feedback_id", "academic_degree", "english_mother_tongue", "language_skill",
                "sentence_difficulty", "similarity_difficulty", "challenge_rating", "challenge_feedback", "challenge_count", "general_feedback"};
        List<String[]> feedbackList = new ArrayList<>();


        for (StudyCombinedChallenge studyCombinedChallenge : studyCombinedChallenges) {

            PairChallenge pairChallenge = studyCombinedChallenge.getPairChallenge();
            ListRankingChallenge listRankingChallenge = studyCombinedChallenge.getListRankingChallenge();

            String[] studyInfo = new String[]{studyCombinedChallenge.getId().toString(),
                    listRankingChallenge.getLemma(),
                    pairChallenge.getIdentifier(),
                    listRankingChallenge.getIdentifier()};
            studyChallenge.add(studyInfo);

            for (StudyUserData studyUserData : studyCombinedChallenge.getStudyUserData()) {

                long feedbackId = studyUserData.getId();
                String pairChallengeReference = "";
                String listChallengeReference = "";




                if (studyUserData.getPairChallengeResults() != null && !studyUserData.getPairChallengeResults().isEmpty()) {
                    try {
                        pairChallengeReference = studyUserData.getId() + "|" + pairChallenge.getId();

                        String pairChallengeResultsString = studyUserData.getPairChallengeResults().
                                replace("\\", "");


                        JSONObject pairChallengeResultsJson = new JSONObject(pairChallengeResultsString).
                                getJSONObject("pairChallengeId");

                        JSONArray userChoicesArray = pairChallengeResultsJson.getJSONArray("userChoices");

                        String[] pairChallengeResult = new String[userChoicesArray.length() + 1];
                        pairChallengeResult[0] = pairChallengeReference;

                        for (int i = 0; i < userChoicesArray.length(); i++) {
                            JSONObject userChoice = userChoicesArray.getJSONObject(i);
                            int id = userChoice.getInt("id");
                            int label = userChoice.getInt("label");

                            UsagePair usagePair = pairChallenge.getUsagePairById(id);
                            String choiceResult = "";
                            try {
                                choiceResult = usagePair.getIdentifier() + ":" + label + ":" + usagePair.getLabel();
                            } catch (NullPointerException e) {
                                logger.error("Error while getting pair challenge choices: ", e);
                            }

                            pairChallengeResult[i + 1] = choiceResult;

                        }
                        pairChallengeResults.add(pairChallengeResult);

                    } catch (JSONException e) {
                        logger.error("Error while converting pair challenge results: ", e);
                    }
                }

                if (studyUserData.getListRankingChallengeResults() != null && !studyUserData.getListRankingChallengeResults().isEmpty()) {
                    try {
                        listChallengeReference = studyUserData.getId() + "|" + listRankingChallenge.getId();
                        String listChallengeResultsString = studyUserData.getListRankingChallengeResults().
                                replace("\\", "");

                        JSONObject listChallengeResultsJson = new JSONObject(listChallengeResultsString).getJSONObject("listChallengeId");
                        JSONArray userOrderArray = listChallengeResultsJson.getJSONArray("order");

                        String[] listChallengeResult = new String[listChallengeResultsHeader.length];
                        String[] tempArray = new String[userOrderArray.length()];

                        listChallengeResult[0] = listChallengeReference;

                        for (int i = 0; i < userOrderArray.length(); i++) {
                            long id = userOrderArray.getLong(i);
                            try {
                                Usage usage = allUsages.stream().filter(u -> u.getId().equals(id)).findFirst().get();
                                tempArray[i] = usage.getIdentifier();
                            } catch (NoSuchElementException e) {
                                logger.error("Error converting getting list challenge order, usage does not exist: ", e);
                            }
                        }
                        listChallengeResult[1] = String.join(",", tempArray);
                        listChallengeResult[2] = listRankingChallenge.getIdentifier();


                        listChallengeResults.add(listChallengeResult);
                    } catch (JSONException e) {
                        logger.error("Error while converting list challenge results: ", e);
                    }
                }
                if (studyUserData.getFeedback() != null && !studyUserData.getFeedback().isEmpty()) {
                    try {
                        //header: {"id", "academic_degree", "english_mother_tongue", "language_skill",
                        //                "sentence_difficulty", "similarity_difficulty", "challenge_rating", "challenge_feedback", "challenge_count", "general_feedback"};
                        String feedbackString = studyUserData.getFeedback();//.replace("\\\"", "\"");

                        JSONObject feedbackJson = new JSONObject(feedbackString);

                        String[] challengeFeedbackArray = feedbackJson.getString("ChallengeFeedback").split("\\|");

                        int challengeRating = 0;
                        if (!challengeFeedbackArray[0].isBlank()) {
                            challengeRating = Integer.parseInt(challengeFeedbackArray[0].trim());
                        }

                        String challengeFeedback = "";
                        if (!challengeFeedbackArray[1].isBlank()) {
                            challengeFeedback = challengeFeedbackArray[1].trim().replace("\n", "").replace("\r", "");
                        }

                        String[] currentFeedback = new String[feedbackHeader.length];

                        currentFeedback[0] = String.valueOf(feedbackId);
                        currentFeedback[1] = feedbackJson.optString("academicDegree", "");
                        currentFeedback[2] = feedbackJson.optString("motherLanguage", "");
                        currentFeedback[3] = String.valueOf(feedbackJson.optInt("languageSkills", 0));
                        currentFeedback[4] = String.valueOf(feedbackJson.optInt("ratingSentenceUnderstanding", 0));
                        currentFeedback[5] = String.valueOf(feedbackJson.optInt("ratingDetermineSemanticMeaning", 0));
                        currentFeedback[6] = String.valueOf(challengeRating);
                        currentFeedback[7] = challengeFeedback;
                        currentFeedback[8] = String.valueOf(feedbackJson.optInt("ChallengeCountOpinion", 0));
                        currentFeedback[9] = feedbackJson.optString("generalFeedback", "").replace("\n", "").replace("\r", "");


                        feedbackList.add(currentFeedback);

                    } catch (JSONException e) {
                        logger.error("Error while converting feedback: {}", studyUserData.getFeedback(), e);
                    }


                    String[] studyResult = new String[]{studyCombinedChallenge.getId().toString(),
                        String.valueOf(studyUserData.getEndTime() - studyUserData.getStartTime()),
                        pairChallengeReference,
                        listChallengeReference,
                        String.valueOf(feedbackId)};
                studyResults.add(studyResult);
                }
            }
        }
        DataWriter dataWriter = new DataWriter();
        dataWriter.writeCSVWithHeader("study-challenges", studyChallengeHeader, studyChallenge);
        dataWriter.writeCSVWithHeader("study-results", studyResultsHeader, studyResults);
        dataWriter.writeCSVWithHeader("pair-challenge-results", pairChallengeResultsHeader, pairChallengeResults);
        dataWriter.writeCSVWithHeader("list-challenge-results", listChallengeResultsHeader, listChallengeResults);
        dataWriter.writeCSVWithHeader("user-feedback", feedbackHeader, feedbackList);

    }
}
