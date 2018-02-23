package com.github.gonzalezjo.shortybot.online.registration;

import io.netty.handler.codec.http.cookie.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.cookie.CookieStore;
import org.asynchttpclient.uri.Uri;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class NoOpCookieStore implements CookieStore {
    private static final List<Cookie> ignored = new ArrayList<>();
    
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass().getName());
    
    @Override
    public void add(Uri uri, Cookie cookie) {
        logger.trace("add called.");
    }
    
    @Override
    public List<Cookie> get(final Uri uri) {
        logger.trace("get called.");
        return ignored;
    }
    
    @Override
    public List<Cookie> getAll() {
        logger.trace("getAll called.");
        return ignored;
    }
    
    @Override
    public boolean remove(final Predicate<Cookie> predicate) {
        logger.trace("remove called.");
        return false;
    }
    
    @Override
    public boolean clear() {
        logger.trace("clear called.");
        return false;
    }
}
