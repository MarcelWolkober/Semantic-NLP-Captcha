package com.nlpcaptcha.captcha.services;

import com.nlpcaptcha.captcha.model.Usage;
import com.nlpcaptcha.captcha.model.UsagePair;
import com.nlpcaptcha.captcha.repository.DataReader;
import com.nlpcaptcha.captcha.repository.UsagePairRepository;
import com.nlpcaptcha.captcha.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Service class for creating pairs of usages
 */
@Service
@Transactional
public class PairService {

    private final UsageRepository usageRepository;
    private final UsagePairRepository usagePairRepository;

    @Autowired
    public PairService(UsageRepository usageRepository, UsagePairRepository usagePairRepository) {
        this.usageRepository = usageRepository;
        this.usagePairRepository = usagePairRepository;
    }


    /**
     * Read data from a file and create pairs of usages
     *
     * @param path Path to the file
     * @return List of created pairs
     */
    @Transactional
    public List<UsagePair> readAndSavePairs(String path) {
        DataReader dataReader = new DataReader();
        List<List<String>> records = dataReader.readData(path);
        records.removeFirst();//remove header

        List<UsagePair> usagePairs = new ArrayList<>();

        for (List<String> record : records) {

            String identifier1 = record.get(1);
            String identifier2 = record.get(2);

            String identifier = identifier1 + "|" + identifier2;


            if (identifier1.equals(identifier2) || usagePairRepository.existsByIdentifier(identifier)) {
                continue;
            }

            Usage existingUsage1 = usageRepository.findByIdentifier(identifier1);
            Usage existingUsage2 = usageRepository.findByIdentifier(identifier2);

            if (existingUsage1 == null) {
                int startIndex1 = Integer.parseInt(record.get(5).split(":")[0]);
                int endIndex1 = Integer.parseInt(record.get(5).split(":")[1]);
                existingUsage1 = new Usage(record.get(0), record.get(1), record.get(3), startIndex1, endIndex1);
                usageRepository.save(existingUsage1);
            }
            if (existingUsage2 == null) {
                int startIndex2 = Integer.parseInt(record.get(6).split(":")[0]);
                int endIndex2 = Integer.parseInt(record.get(6).split(":")[1]);
                existingUsage2 = new Usage(record.get(0), record.get(2), record.get(4), startIndex2, endIndex2);
                usageRepository.save(existingUsage2);
            }

            float label = Float.parseFloat(record.get(7));
            UsagePair pair = createAndSavePairByUsageIdentifiers(identifier1, identifier2, label);
            usagePairs.add(pair);
        }
        return usagePairs;

    }


    /**
     * Create and Save a pair of two usages by their identifiers as UsagePair
     * REQUIRES: The usages with the given identifiers exist in the database
     *
     * @param identifier1 Identifier of the first usage
     * @param identifier2 Identifier of the second usage
     * @param label       Label of the pair
     * @return The created pair as UsagePair object
     * @throws IllegalArgumentException If one or both of the usages do not exist in the database
     */
    @Transactional
    public UsagePair createAndSavePairByUsageIdentifiers(String identifier1, String identifier2, float label) throws IllegalArgumentException {
        Usage existingUsage1 = usageRepository.findByIdentifier(identifier1);
        Usage existingUsage2 = usageRepository.findByIdentifier(identifier2);

        if (existingUsage1 == null | existingUsage2 == null) {
            throw new IllegalArgumentException("One or both of the usages do not exist in the database");
        }

        String identifier = identifier1 + "|" + identifier2;
        UsagePair usagePair = new UsagePair(identifier, existingUsage1, existingUsage2, label);
        UsagePair savedPair =  usagePairRepository.save(usagePair);

        List<UsagePair> db = usagePairRepository.findAll();
        List<Usage> debug = usageRepository.findAll();

        // Check if the UsagePair has more than two Usages
        if (usagePair.getUsages().size() > 2) {
            throw new IllegalArgumentException("A UsagePair can only have two Usages");
        }
        // Save the Usage objects again after setting the UsagePair
        usageRepository.save(existingUsage1);
        usageRepository.save(existingUsage2);

        debug = usageRepository.findAll();

        // Save the UsagePair
        return savedPair;

    }

    @Transactional
    public void deletePair(UsagePair usagePair) {
        Set<Usage> usages = usagePair.getUsages();

        // Remove the association between the UsagePair and its Usages
        for (Usage usage : usages) {
            if (usage.getUsagePairs().contains(usagePair)) {
                usage.removeUsagePair(usagePair);
                usageRepository.save(usage);
            }
        }
        // Delete the UsagePair
        usagePairRepository.delete(usagePair);
    }
}
