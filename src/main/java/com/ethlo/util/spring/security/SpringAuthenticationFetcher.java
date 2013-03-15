package com.ethlo.util.spring.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 * 
 * @author mha
 */
public class SpringAuthenticationFetcher
{
	//private static final Logger logger = LoggerFactory.getLogger(SpringAuthenticationFetcher.class);
	private static final String securityContextHolderClassname = "org.springframework.security.core.context.SecurityContextHolder";
	private static final String securityContextClassname = "org.springframework.security.core.context.SecurityContext";
	//private static final String authenticationClassname = "org.springframework.security.core.Authentication";
	private static Method getContextMethod;
	private static Method getAuthMethod;
	private static boolean available = true;
	
	private SpringAuthenticationFetcher()
	{
		
	}
	
	static
	{
		try
		{
			final Class<?> securityContextHolderClass = Class.forName(securityContextHolderClassname, false, SpringAuthenticationFetcher.class.getClassLoader());
			getContextMethod = securityContextHolderClass.getMethod("getContext", new Class<?>[0]);
			
		    final Class<?> securityContextClass = Class.forName(securityContextClassname, false, SpringAuthenticationFetcher.class.getClassLoader());
			getAuthMethod = securityContextClass.getDeclaredMethod("getAuthentication", new Class<?>[0]);
		}
		catch (Exception exc)
		{
			available = false;
		}
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
	
	public static String getUsername()
	{
		final Principal auth = getAuthentication(Principal.class);
		if (auth != null)
		{
			return auth.getName();
		}
		return null;
	}
}
