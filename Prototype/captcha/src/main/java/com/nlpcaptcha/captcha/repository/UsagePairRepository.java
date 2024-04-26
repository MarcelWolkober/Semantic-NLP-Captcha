package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.UsagePair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsagePairRepository extends JpaRepository<UsagePair, Long> {

}
