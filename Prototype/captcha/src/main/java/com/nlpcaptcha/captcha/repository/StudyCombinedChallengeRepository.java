package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.StudyCombinedChallenge;
import com.nlpcaptcha.captcha.model.UsagePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCombinedChallengeRepository extends JpaRepository<StudyCombinedChallenge, Long> {

    StudyCombinedChallenge findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);


    StudyCombinedChallenge findFirstByStudyUserDataIsNull();

    List<StudyCombinedChallenge> findAllByStudyUserDataIsNull();
}