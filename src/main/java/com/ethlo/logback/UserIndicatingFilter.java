package com.ethlo.logback;

import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import com.ethlo.util.spring.security.SpringAuthenticationFetcher;

/**
 * 
 * @author mha
 *
 */
public class UserIndicatingFilter extends Filter<ILoggingEvent> 
{
    @Override
    public FilterReply decide(ILoggingEvent eventObject) 
    {
    	MDC.put("username", SpringAuthenticationFetcher.isAvailable() ? SpringAuthenticationFetcher.getUsername() : "?");
        return FilterReply.NEUTRAL;
    }
}