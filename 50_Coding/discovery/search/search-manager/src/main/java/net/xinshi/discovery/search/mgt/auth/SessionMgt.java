package net.xinshi.discovery.search.mgt.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 4/7/13
 * Time: 4:46 PM
 */
public class SessionMgt {
    public static Cache<String, Map<String,String>> sessions = CacheBuilder.newBuilder()
            .maximumSize(1000000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();;
}
