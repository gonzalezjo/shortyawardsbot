package com.github.gonzalezjo.shortybot;

import com.github.gonzalezjo.shortybot.online.registration.AccountGenerator;
import com.github.gonzalezjo.shortybot.online.VoteReadyAccount;
import com.github.gonzalezjo.shortybot.online.dataproviders.AccountNameProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.AccountPasswordProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.IpAddressProvider;

import java.util.concurrent.CompletableFuture;

public class BotWorker implements Runnable {
    private final AccountGenerator accountGenerator;
    private final BotManager botManager;
    
    public BotWorker(final AccountNameProvider nameProvider,
                     final AccountPasswordProvider passwordProvider,
                     final IpAddressProvider addressProvider,
                     final String target,
                     final BotManager botManager) {
        this.botManager = botManager;
        
        this.accountGenerator = new AccountGenerator(nameProvider,
                                                     passwordProvider,
                                                     addressProvider,
                                                     botManager,
                                                     this,
                                                     target);
    }
    
    public void run() {
        getAccount().thenApplyAsync(account -> accountGenerator.get().whenCompleteAsync((v, ignored) -> v.run()));
    }
    
    private CompletableFuture<CompletableFuture<VoteReadyAccount>> getAccount() {
        return CompletableFuture.supplyAsync(accountGenerator, botManager.getPool());
    }
}
