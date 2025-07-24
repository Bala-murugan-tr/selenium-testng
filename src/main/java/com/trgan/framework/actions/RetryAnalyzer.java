package com.trgan.framework.actions;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.trgan.exceptions.ConfigException;
import com.trgan.exceptions.ContextException;
import com.trgan.exceptions.ExcelException;
import com.trgan.framework.config.FrameworkProperties;

public class RetryAnalyzer implements IRetryAnalyzer {
	private int retryCount = 0;
	private static final int maxRetryCount = FrameworkProperties.retryTestMaxAttempt();

	public int getRetryCount() {
		return retryCount;
	}

	@Override
	public boolean retry(ITestResult result) {
		Throwable cause = result.getThrowable();
		// Don't retry known non-retriable failures
		if (cause instanceof ExcelException || cause instanceof ConfigException || cause instanceof ContextException) {
			return false;
		}
		result.setAttribute("retryCount", retryCount);
		return retryCount++ < maxRetryCount;
	}
}
