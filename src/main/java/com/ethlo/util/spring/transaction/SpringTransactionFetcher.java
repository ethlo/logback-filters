package com.ethlo.util.spring.transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Morten Haraldsen
 */
public class SpringTransactionFetcher
{
	private static boolean available = true;
	private static final String txnSyncManagerClass = "org.springframework.transaction.support.TransactionSynchronizationManager";
	private static final String methodName = "isActualTransactionActive";
	private static Method method;
	private static Class<?> clazz;

	private SpringTransactionFetcher()
	{
		
	}
	
	public static Boolean isTransactionActive()
	{
		try
		{
			return Boolean.class.cast(method.invoke(null, (Object[])null));
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
			clazz = Class.forName(txnSyncManagerClass, false, SpringTransactionFetcher.class.getClassLoader());
			method = clazz.getMethod(methodName, new Class<?>[0]);
		}
		catch (Exception e)
		{
			available = false;
		}
	}
}
