package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRepository extends JpaRepository<Usage, Long> {

}
