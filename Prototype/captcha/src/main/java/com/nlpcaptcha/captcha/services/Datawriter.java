package com.nlpcaptcha.captcha.services;


import com.nlpcaptcha.captcha.controller.UsageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class Datawriter {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    private static final String FILE_NAME = "/data/receivedStudyChallenge.txt";

    public void writeToFile(String data) {
        Path path = Paths.get(FILE_NAME);
        try {
            // Use StandardOpenOption.APPEND to append data to the existing file
            Files.writeString(path, data + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
           logger.error("An error occurred while writing to the file: ", e);
        }
    }
}