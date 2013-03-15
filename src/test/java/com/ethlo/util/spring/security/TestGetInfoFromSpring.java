package com.ethlo.util.spring.security;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ethlo.util.spring.transaction.SpringTransactionFetcher;

/**
 * 
 * @author Morten Haraldsen
 */
public class TestGetInfoFromSpring
{
	@Test
	public void testGetAuthentication()
	{
		// When
		final Authentication expectedAuth = new UsernamePasswordAuthenticationToken("smith", new Object());
		SecurityContextHolder.getContext().setAuthentication(expectedAuth);
		
		// Then
		Assert.assertTrue(SpringAuthenticationFetcher.isAvailable());
		final Authentication auth = SpringAuthenticationFetcher.getAuthentication(Authentication.class);
		Assert.assertEquals(expectedAuth, auth);
	}
	
	@Test
	public void testGetTransactionActive()
	{
		Assert.assertTrue(SpringTransactionFetcher.isAvailable());
		Assert.assertFalse(SpringTransactionFetcher.isTransactionActive());
	}
}
