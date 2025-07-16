package com.demo.ailog.processor.consumer.parser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogHashGenerator {

    public static String generateLogHash(String rawlog) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(rawlog.getBytes(StandardCharsets.UTF_8));
        return byteToHex(hash);
    }

    private static String byteToHex(byte[] hash) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b :hash) stringBuilder.append(String.format("%02x", b));
        return stringBuilder.toString();
    }
}
