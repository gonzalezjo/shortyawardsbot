package com.github.gonzalezjo.shortybot.online;

import com.github.gonzalezjo.shortybot.BotWorker;
import com.github.gonzalezjo.shortybot.online.dataproviders.IpAddressProvider;
import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;

import java.util.List;

public class VoteReadyAccount implements Runnable {
    
    private static final String voteUrl = "http://shortyawards.com/voting/vote.json";
    private final AsyncHttpClient httpClient;
    private final List<Cookie> accountCookies;
    private final IpAddressProvider addressProvider;
    private final String target;
    private final BotWorker botWorker;
    
    public VoteReadyAccount(final List<Cookie> accountCookies,
                            final AsyncHttpClient httpClient,
                            final IpAddressProvider addressProvider,
                            final BotWorker botWorker,
                            final String target) {
        this.accountCookies = accountCookies;
        this.httpClient = httpClient;
        this.addressProvider = addressProvider;
        this.target = target;
        this.botWorker = botWorker;
    }
    
    public void run() {
        final String xCsrfToken = accountCookies.stream()
                .filter(c -> c.name().equals("csrftoken"))
                .findFirst()
                .get()
                .value();
        
        accountCookies.removeIf(s -> s.name().equals("csrftoken") && !s.value().equals(xCsrfToken));
        
        final BoundRequestBuilder request = httpClient.preparePost(voteUrl)
                .addFormParam("entry_slug", target)
                .addFormParam("category_slug", "youtuber")
                .addFormParam("vote_type", "click")
                .addHeader("X-FORWARDED-FOR", addressProvider.getIpAddress())
                .addHeader("Via", addressProvider.getIpAddress())
                .addHeader("Client-Ip", addressProvider.getIpAddress())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-CSRFTOKEN", xCsrfToken);
        
        accountCookies.forEach(request::addCookie);
        
        request.execute().toCompletableFuture().thenRunAsync(botWorker);
    }
}
