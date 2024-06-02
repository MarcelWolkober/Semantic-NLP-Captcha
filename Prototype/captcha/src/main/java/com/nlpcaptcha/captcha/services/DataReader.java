package com.nlpcaptcha.captcha.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataReader {
    private static final String TAB_DELIMITER = "\t";


    public List<List<String>> readData(String path) {
        List<List<String>> records = new ArrayList<>();

        String absolute_path = new File(".").getAbsolutePath();
        if(absolute_path.contains("/opt/app")){
            path = "../../data/" + path;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {



            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(TAB_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return records;
    }


}
