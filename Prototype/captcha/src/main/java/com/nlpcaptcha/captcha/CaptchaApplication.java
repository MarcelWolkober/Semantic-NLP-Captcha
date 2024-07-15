package com.nlpcaptcha.captcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CaptchaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(CaptchaApplication.class, args);

    }


}
