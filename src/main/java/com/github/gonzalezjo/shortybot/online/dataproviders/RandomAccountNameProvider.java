package com.github.gonzalezjo.shortybot.online.dataproviders;

import java.security.SecureRandom;

public class RandomAccountNameProvider implements AccountNameProvider {
    private static final int NAME_LENGTH = 9;
    private static final SecureRandom randomNumberGenerator = new SecureRandom();
    private static final StringBuilder stringBuilder = new StringBuilder(NAME_LENGTH);
    private static final String EMAIL_PROVIDER_SUFFIX = "@gmail.com";
    
    @Override
    public String getName() {
        return randomName();
    }
    
    private synchronized String randomName() {
        stringBuilder.delete(0, stringBuilder.length());
        
        for (int i = 0; i < NAME_LENGTH; i++) {
            stringBuilder.append((char) (randomNumberGenerator.nextInt(26) + 'a'));
        }
        
        return stringBuilder.append(EMAIL_PROVIDER_SUFFIX).toString();
    }
}
