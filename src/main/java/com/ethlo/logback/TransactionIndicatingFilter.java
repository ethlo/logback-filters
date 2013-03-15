package com.ethlo.logback;

import org.slf4j.MDC;

import com.ethlo.util.spring.transaction.SpringTransactionFetcher;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 
 * @author Morten Haraldsen
 */
public class TransactionIndicatingFilter extends Filter<ILoggingEvent>
{
	@Override
    public FilterReply decide(ILoggingEvent eventObject) 
    {
		MDC.put("txnStatus", SpringTransactionFetcher.isAvailable() ? (SpringTransactionFetcher.isTransactionActive() ? "+" : "-") : "?");
        return FilterReply.NEUTRAL;
    }
}
