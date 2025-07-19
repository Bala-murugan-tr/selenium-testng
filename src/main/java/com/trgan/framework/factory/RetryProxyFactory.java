package com.trgan.framework.factory;

import java.lang.reflect.Proxy;

import com.trgan.framework.actions.RetryHandler;

public class RetryProxyFactory {

	@SuppressWarnings("unchecked")
	public static <T> T createWithRetry(Class<T> interfaceType, T target, int maxAttempts, int delayMs) {
		return (T) Proxy.newProxyInstance(
				interfaceType.getClassLoader(),
				new Class<?>[] { interfaceType },
				(proxy, method, args) -> RetryHandler.retry(() -> {
					try {
						return method.invoke(target, args);
					} catch (Exception e) {
						throw e;
					}
				}, maxAttempts, delayMs, method.getName()));
	}
}
