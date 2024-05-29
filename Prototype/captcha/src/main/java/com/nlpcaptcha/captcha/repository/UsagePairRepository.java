package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.UsagePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsagePairRepository extends JpaRepository<UsagePair, Long> {

    public UsagePair findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}
