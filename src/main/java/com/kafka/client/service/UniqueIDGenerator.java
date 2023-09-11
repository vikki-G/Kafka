package com.kafka.client.service;

import java.util.UUID;

public class UniqueIDGenerator {
    public String idGenerate() {
        String uniqueID = generateUniqueID(12, 15);
        System.out.println("Unique ID: " + uniqueID);
		return uniqueID;
    }

    public static String generateUniqueID(int minLength, int maxLength) {
        if (minLength < 1 || maxLength < minLength) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        int length = uuid.length();

        if (length < minLength) {
            throw new IllegalArgumentException("Minimum length not achievable");
        }

        if (length <= maxLength) {
            return uuid.substring(0, maxLength);
        }

        return uuid.substring(0, length);
    }
}
