package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {

 public Usage findByIdentifier(String identifier);

 public boolean existsByIdentifier(String identifier);
}



