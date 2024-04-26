package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.PairChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PairChallengeRepository extends JpaRepository<PairChallenge, Long> {

}
