package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PairService {

    private final UsageRepository usageRepository;

    @Autowired
    public PairService(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }


    /**
     * Read data from a file and create pairs of usages
     *
     * @param path Path to the file
     * @return List of created pairs
     */
    public List<UsagePair> readData(String path) {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header

        List<UsagePair> usagePairs = new ArrayList<>();

        for (List<String> record : records) {

            String identifier1 = record.get(1);
            String identifier2 = record.get(2);

            Usage existingUsage1 = usageRepository.findByIdentifier(identifier1);
            Usage existingUsage2 = usageRepository.findByIdentifier(identifier2);

            if (existingUsage1 == null) {
                int startIndex1 = Integer.parseInt(record.get(5).split(":")[0]);
                int endIndex1 = Integer.parseInt(record.get(5).split(":")[1]);
                existingUsage1 = new Usage(record.get(0), record.get(1), record.get(3), startIndex1, endIndex1);
                // usageRepository.save(usage1);
            }
            if (existingUsage2 == null) {
                int startIndex2 = Integer.parseInt(record.get(6).split(":")[0]);
                int endIndex2 = Integer.parseInt(record.get(6).split(":")[1]);
                existingUsage2 = new Usage(record.get(0), record.get(2), record.get(4), startIndex2, endIndex2);
                //usageRepository.save(usage2);
            }

            String identifier = identifier1 + "|" + identifier2;
            float label = Float.parseFloat(record.get(7));
            UsagePair pair = new UsagePair(identifier, existingUsage1, existingUsage2, label);
            usagePairs.add(pair);
        }
        return usagePairs;

    }


    /**
     * Create a pair of two usages by their identifiers
     * REQUIRES: The usages with the given identifiers exist in the database
     *
     * @param identifier1 Identifier of the first usage
     * @param identifier2 Identifier of the second usage
     * @param label       Label of the pair
     * @return The created pair as UsagePair object
     * @throws IllegalArgumentException If one or both of the usages do not exist in the database
     */
    public UsagePair createPairByUsageIdentifiers(String identifier1, String identifier2, float label) throws IllegalArgumentException {
        Usage existingUsage1 = usageRepository.findByIdentifier(identifier1);
        Usage existingUsage2 = usageRepository.findByIdentifier(identifier2);

        if (existingUsage1 == null | existingUsage2 == null) {
            throw new IllegalArgumentException("One or both of the usages do not exist in the database");
        }

        String identifier = identifier1 + "|" + identifier2;
        return new UsagePair(identifier, existingUsage1, existingUsage2, label);
    }
}
