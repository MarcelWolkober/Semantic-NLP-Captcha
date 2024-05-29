package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.PairChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PairChallengeRepository extends JpaRepository<PairChallenge, Long> {

    boolean existsByIdentifier(String identifier);

    PairChallenge findByIdentifier(String identifier);
}
