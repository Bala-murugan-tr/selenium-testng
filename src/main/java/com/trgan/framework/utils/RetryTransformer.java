package com.trgan.framework.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.trgan.framework.actions.RetryAnalyzer;
import com.trgan.framework.config.FrameworkProperties;

@SuppressWarnings("rawtypes")
public class RetryTransformer implements IAnnotationTransformer {
	boolean retryTest = FrameworkProperties.retryTest();

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		// Skip retry if the test is explicitly tagged with 'noretry'
		if (retryTest && !Arrays.asList(annotation.getGroups()).contains("noretry")) {
			annotation.setRetryAnalyzer(RetryAnalyzer.class);
		}
	}
}
