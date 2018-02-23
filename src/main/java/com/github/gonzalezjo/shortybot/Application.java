package com.github.gonzalezjo.shortybot;

import com.github.gonzalezjo.shortybot.online.dataproviders.*;

class Application {
    private static final int THREAD_COUNT = 16;
    private static final AccountNameProvider nameProvider = new RandomAccountNameProvider();
    private static final AccountPasswordProvider passwordProvider = new RandomAccountPasswordProvider();
    private static final IpAddressProvider addressProvider = new RandomIpV4AddressProvider();
    private static final String TARGET_USER = "jacksfilms";
    
    public static void main(String[] args) {
        new BotManager(nameProvider, passwordProvider, addressProvider, THREAD_COUNT, TARGET_USER);
    }
}
