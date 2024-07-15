package com.nlpcaptcha.captcha.repository;

import com.nlpcaptcha.captcha.model.StudyUserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyUserDataRepository extends JpaRepository<StudyUserData, Long> {
}
