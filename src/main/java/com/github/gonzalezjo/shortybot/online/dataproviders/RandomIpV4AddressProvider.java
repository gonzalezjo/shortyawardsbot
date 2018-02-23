package com.github.gonzalezjo.shortybot.online.dataproviders;

import java.security.SecureRandom;

public class RandomIpV4AddressProvider implements IpAddressProvider {
    private final static SecureRandom randomNumberGenerator = new SecureRandom();
    private final static StringBuilder stringBuilder = new StringBuilder();
    
    @Override
    public String getIpAddress() {
        return randomIpV4Address();
    }
    
    private synchronized String randomIpV4Address() {
        stringBuilder.delete(0, stringBuilder.length());
        
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(randomNumberGenerator.nextInt(255));
            stringBuilder.append(".");
        }
        
        stringBuilder.append(randomNumberGenerator.nextInt(255));
        
        return stringBuilder.toString();
    }
}
