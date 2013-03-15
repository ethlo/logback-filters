package com.ethlo.logback;

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import com.ethlo.util.spring.transaction.SpringTransactionFetcher;

/**
 * 
 * @author Morten Haraldsen
 */
public class TransactionIndicatingTurboFilter extends TurboFilter
{
	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
	{
		MDC.put("txnStatus", SpringTransactionFetcher.isAvailable() ? (SpringTransactionFetcher.isTransactionActive() ? "+" : "-") : "?");
        return FilterReply.NEUTRAL;
	}
}
