package com.trgan.framework.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.trgan.framework.actions.RetryAnalyzer;
import com.trgan.framework.config.FrameworkProperties;

public class RetryTransformer implements IAnnotationTransformer {
	boolean retryTest = FrameworkProperties.retryTest();

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		// won't retry the test, if it has noretry tag
		if (retryTest && !Arrays.asList(annotation.getGroups()).contains("noretry")) {
			annotation.setRetryAnalyzer(RetryAnalyzer.class);
		}
	}
}
