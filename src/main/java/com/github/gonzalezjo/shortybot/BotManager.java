package com.github.gonzalezjo.shortybot;

import com.github.gonzalezjo.shortybot.online.dataproviders.AccountNameProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.AccountPasswordProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.IpAddressProvider;

import java.util.concurrent.ForkJoinPool;

public class BotManager {
    private final ForkJoinPool pool = new ForkJoinPool();
    
    public BotManager(final AccountNameProvider nameProvider,
                      final AccountPasswordProvider passwordProvider,
                      final IpAddressProvider addressProvider,
                      final int threadCount,
                      final String target) { // should be a builder but...
        
        for (int i = 0; i < threadCount; i++) {
            pool.execute(new BotWorker(nameProvider, passwordProvider, addressProvider, target, this));
        }
    }
    
    public ForkJoinPool getPool() {
        return pool;
    }
}
