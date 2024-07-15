package com.nlpcaptcha.captcha.services;


import com.nlpcaptcha.captcha.controller.UsageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;

public class DataWriter {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    private static final String CSV_SEPARATOR = "\t";


    public void writeToFile(String data) {
        final String FILE_NAME = "/data/output/receivedStudyChallenge.txt";
        Path path = Paths.get(FILE_NAME);
        try {
            // Use StandardOpenOption.APPEND to append data to the existing file
            Files.writeString(path, data + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("An error occurred while writing to the file: ", e);
        }
    }

    public void writeCSVWithHeader(String fileName, String[] header, List<String[]> data) {
        final String filePath = "/data/output/"+ fileName +".csv";
        Path path = Paths.get(filePath);

        try {
            // Write the header
            Files.writeString(path, String.join(CSV_SEPARATOR, header) + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Write the data
            for (String[] record : data) {
                Files.writeString(path, String.join(CSV_SEPARATOR, record) + System.lineSeparator(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            logger.error("An error occurred while writing to the file: ", e);
        }
    }
}