package com.ethlo.util.spring.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author mha
 */
public class SpringAuthenticationFetcher
{
	private static final String springThreadLocalHolderClass = "org.springframework.security.core.context.SecurityContextHolder";
	private static Method getContextMethod;
	private static Method getAuthMethod;
	private static Class<?> clazz;
	private static boolean available = true;
	
	private SpringAuthenticationFetcher()
	{
		
	}
	
	public static <T> T getAuthentication(Class<T> type)
	{
		try
		{
			final Object context = getContextMethod.invoke(null, (Object[])null);
			final Object auth = getAuthMethod.invoke(context, (Object[]) null);
			return type.cast(auth);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			return null;
		}
	}
	
	public static boolean isAvailable()
	{
		return available;
	}
	
	static
	{
		try
		{
			clazz = Class.forName(springThreadLocalHolderClass, false, SpringAuthenticationFetcher.class.getClassLoader());
			getContextMethod = clazz.getMethod("getContext", new Class<?>[0]);
		    final Object context = getContextMethod.invoke(null, (Object[])null);
		    getAuthMethod = context.getClass().getDeclaredMethod("getAuthentication", new Class<?>[0]);
		}
		catch (Exception exc)
		{
			available = false;
		}
	}
}
