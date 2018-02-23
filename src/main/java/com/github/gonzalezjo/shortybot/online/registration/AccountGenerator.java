package com.github.gonzalezjo.shortybot.online.registration;

import com.github.gonzalezjo.shortybot.BotManager;
import com.github.gonzalezjo.shortybot.BotWorker;
import com.github.gonzalezjo.shortybot.online.VoteReadyAccount;
import com.github.gonzalezjo.shortybot.online.dataproviders.AccountNameProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.AccountPasswordProvider;
import com.github.gonzalezjo.shortybot.online.dataproviders.IpAddressProvider;
import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class AccountGenerator implements Supplier<CompletableFuture<VoteReadyAccount>> {
    private static final String REGISTRATION_API = "https://shortyawards" + ".com/account/signup";
    private static final String DEFAULT_CSRF_TOKEN = "xtrIgMJ4uvKJuAyxjwGbzmnvwgXS8ZgzdgktqvlGRFhYjfxmH6BOFolZLoXODyKP";
    private static final String SUBSCRIPTION_VALUE = "off";
    private static final String NEXT_VALUE = "/";
    
    private static final AsyncHttpClient httpClient = Dsl.asyncHttpClient(Dsl.config()
                                                                                  .setCookieStore(new NoOpCookieStore())
                                                                                  .setCompressionEnforced(true)
                                                                                  .setMaxConnections(64)
                                                                                  .setMaxConnectionsPerHost(64));
    
    private final AccountNameProvider nameProvider;
    private final AccountPasswordProvider passwordProvider;
    private final IpAddressProvider addressProvider;
    private final BotManager botManager;
    private final BotWorker botWorker;
    private final String target;
    
    public AccountGenerator(final AccountNameProvider nameProvider,
                            final AccountPasswordProvider passwordProvider,
                            final IpAddressProvider addressProvider,
                            final BotManager botManager,
                            final BotWorker botWorker,
                            final String target) {
        this.nameProvider = nameProvider;
        this.passwordProvider = passwordProvider;
        this.addressProvider = addressProvider;
        this.botManager = botManager;
        this.botWorker = botWorker;
        this.target = target;
    }
    
    @Override
    public CompletableFuture<VoteReadyAccount> get() {
        final List<Cookie> session = new ArrayList<>();
        
        return CompletableFuture.supplyAsync(() -> loadCsrfTokens(session), botManager.getPool())
                .thenApplyAsync(s -> associateAccount(s, session));
    }
    
    private CompletableFuture<String> loadCsrfTokens(final List<Cookie> session) {
        String outcome = "";
        
        try {
            final Response response = httpClient.prepareGet(REGISTRATION_API)
                    .addHeader("X-FORWARDED-FOR", addressProvider.getIpAddress())
                    .addHeader("Via", addressProvider.getIpAddress())
                    .addHeader("Client-Ip", addressProvider.getIpAddress())
                    .execute()
                    .get();
            
            session.addAll(response.getCookies());
            
            outcome = response.getResponseBody()
                    .replaceFirst("(?s).*csrfmiddlewaretoken", "")
                    .replaceFirst(".*value='", "")
                    .replaceFirst("(?s)'.*", "");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.exit(1337);
        }
        
        return CompletableFuture.completedFuture(outcome);
    }
    
    private VoteReadyAccount associateAccount(final CompletableFuture<String> csrfToken, final List<Cookie> session) {
        final String username, password;
        username = nameProvider.getName();
        password = passwordProvider.getPassword();
        
        final ListenableFuture<Response> registrationRequest = httpClient.preparePost(
                "https://shortyawards.com/account/signup")
                .addFormParam("csrfmiddlewaretoken", csrfToken.getNow(DEFAULT_CSRF_TOKEN))
                .addFormParam("next", NEXT_VALUE)
                .addFormParam("email", username)
                .addFormParam("password1", password)
                .addFormParam("subscribe", SUBSCRIPTION_VALUE)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", REGISTRATION_API)
                .addHeader("X-FORWARDED-FOR", addressProvider.getIpAddress())
                .addHeader("Via", addressProvider.getIpAddress())
                .addHeader("Client-Ip", addressProvider.getIpAddress())
                .setCookies(session)
                .execute();
        
        try {
            session.addAll(registrationRequest.get().getCookies());
        } catch (InterruptedException | ExecutionException ignored) {
        }
        
        return new VoteReadyAccount(session, httpClient, addressProvider, botWorker, target);
    }
}
