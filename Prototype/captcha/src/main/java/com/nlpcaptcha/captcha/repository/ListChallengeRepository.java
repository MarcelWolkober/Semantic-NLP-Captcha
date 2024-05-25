package com.nlpcaptcha.captcha.repository;


import com.nlpcaptcha.captcha.model.ListRankingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ListChallengeRepository extends JpaRepository<ListRankingChallenge, Long> {

}
