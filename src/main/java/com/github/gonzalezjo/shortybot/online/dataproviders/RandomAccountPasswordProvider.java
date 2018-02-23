package com.github.gonzalezjo.shortybot.online.dataproviders;

import java.security.SecureRandom;

public class RandomAccountPasswordProvider implements AccountPasswordProvider {
    private static final int PASSWORD_LENGTH = 9;
    private static final SecureRandom randomNumberGenerator = new SecureRandom();
    private static final StringBuilder stringBuilder = new StringBuilder(PASSWORD_LENGTH);
    
    @Override
    public String getPassword() {
        return randomPassword();
    }
    
    private String randomPassword() {
        stringBuilder.delete(0, stringBuilder.length());
        
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            stringBuilder.append((char) (randomNumberGenerator.nextInt(26) + 'a'));
        }
        
        return stringBuilder.toString();
    }
}
