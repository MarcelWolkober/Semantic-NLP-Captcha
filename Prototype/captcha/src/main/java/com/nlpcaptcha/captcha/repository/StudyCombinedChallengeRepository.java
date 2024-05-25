package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.UsagePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyCombinedChallengeRepository extends JpaRepository<UsagePair, Long> {

}