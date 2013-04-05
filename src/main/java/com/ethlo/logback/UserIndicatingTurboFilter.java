package com.ethlo.logback;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import com.ethlo.util.spring.security.SpringAuthenticationFetcher;

/**
 * 
 * @author mha
 *
 */
public class UserIndicatingTurboFilter extends TurboFilter
{
	private final org.slf4j.Logger systemLogger = LoggerFactory.getLogger(UserIndicatingTurboFilter.class);
	private String prefix = "";
	private String[] extraPropertyNames;

	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
	{
		MDC.put("username", SpringAuthenticationFetcher.isAvailable() ? SpringAuthenticationFetcher.getUsername() : "?");
		
		if (SpringAuthenticationFetcher.isAvailable() && extraPropertyNames != null)
		{
			final Object auth = SpringAuthenticationFetcher.getAuthentication(Object.class);
			if (auth != null)
			{
				for (String extraPropertyName : extraPropertyNames)
				{
					try
					{
						final BeanInfo info = Introspector.getBeanInfo(auth.getClass());
						final PropertyDescriptor[] pds = info.getPropertyDescriptors();
						final Method m = findMethod(extraPropertyName, pds);
						if (m != null)
						{
							final Object extraPropertyValue = m.invoke(auth, new Object[0]);
							MDC.put(prefix + extraPropertyName, extraPropertyValue.toString());
						}
					}
					catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exc)
					{
						systemLogger.warn("", exc);
					}
				}
			}
		}
		
        return FilterReply.NEUTRAL;
	}

	private Method findMethod(String extraPropertyName, PropertyDescriptor[] pds)
	{
		for (PropertyDescriptor desc : pds)
		{
			if (desc.getName().equals(extraPropertyName))
			{
				return desc.getReadMethod();
			}
		}
		return null;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setExtraPropertyNames(String[] extraPropertyNames)
	{
		this.extraPropertyNames = extraPropertyNames;
	}
}