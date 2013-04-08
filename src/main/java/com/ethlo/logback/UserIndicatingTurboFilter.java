package com.ethlo.logback;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

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
	private Map<String, String> extraProperties;

	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
	{
		MDC.put("username", SpringAuthenticationFetcher.isAvailable() ? SpringAuthenticationFetcher.getUsername() : "?");
		
		if (SpringAuthenticationFetcher.isAvailable() && extraProperties != null)
		{
			final Object auth = SpringAuthenticationFetcher.getAuthentication(Object.class);
			if (auth != null)
			{
				for (Entry<String, String> extraProperty : extraProperties.entrySet())
				{
					try
					{
						final BeanInfo info = Introspector.getBeanInfo(auth.getClass());
						final PropertyDescriptor[] pds = info.getPropertyDescriptors();
						final String beanProperty = extraProperty.getValue();
						final Method m = findMethod(beanProperty, pds);
						if (m != null)
						{
							final Object extraPropertyValue = m.invoke(auth, new Object[0]);
							MDC.put(extraProperty.getKey(), extraPropertyValue.toString());
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

	public void setExtraProperties(Map<String, String> extraProperties)
	{
		this.extraProperties = extraProperties;
	}
}