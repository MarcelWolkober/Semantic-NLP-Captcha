package com.nlpcaptcha.captcha.repository;


import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ListChallengeRepository extends JpaRepository<ListRankingChallenge, Long> {

}
