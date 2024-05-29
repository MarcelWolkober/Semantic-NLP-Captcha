package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Configurable
public class UsageService {

   private final UsageRepository usageRepository;

    @Autowired
    public UsageService(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

    //UsageRepositoryImpl usageRepositoryImpl = new UsageRepositoryImpl();

    public List<Usage> readData(String path) {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header

        List<Usage> usages = new ArrayList<>();

        for (List<String> record : records) {//TODO: map record to Usage

            String identifier = record.get(1);
            Usage existingUsage = usageRepository.findByIdentifier(identifier);
            int startIndex = Integer.parseInt(record.get(3).split(":")[0]);
            int endIndex = Integer.parseInt(record.get(3).split(":")[1]);

            if (existingUsage == null) {

                Usage usage = new Usage(record.get(0), identifier, record.get(2), startIndex, endIndex);
                usages.add(usage);
            }
        }
        return usages;
    }
}
