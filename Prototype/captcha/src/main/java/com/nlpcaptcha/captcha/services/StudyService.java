package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.repository.StudyCombinedChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudyService {

    private final StudyCombinedChallengeRepository studyCombinedChallengeRepository;
    private final PairChallengeService pairChallengeService;
    private final ListChallengeService listRankingChallengeService;

    @Autowired
    public StudyService(StudyCombinedChallengeRepository studyCombinedChallengeRepository) {
        this.studyCombinedChallengeRepository = studyCombinedChallengeRepository;
        this.pairChallengeService = new PairChallengeService();
        this.listRankingChallengeService = new ListChallengeService();
    }



    @Transactional
    public StudyCombinedChallenge saveStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        return studyCombinedChallenge;
    }
}
