package com.trgan.framework.context;

import com.trgan.exceptions.ContextException;
import com.trgan.framework.utils.TestLogger;

/**
 * Manages thread-local storage for {@link TestContext} instances during
 * parallel test execution.
 * <p>
 * Provides centralized access to test-specific contexts (driver, report, Excel,
 * metadata), ensuring isolation and traceability across threads.
 */
public class TestContextManager {
	// Holds the TestContext per executing thread to enable safe parallel execution.
	private static final ThreadLocal<TestContext> testContext = new ThreadLocal<>();

	/**
	 * Sets the current thread's {@link TestContext}.
	 *
	 * @param context The test context to associate with the current thread.
	 */
	public static void setContext(TestContext context) {
		testContext.set(context);
	}

	/**
	 * Retrieves the {@link TestContext} associated with the current thread.
	 *
	 * @return The current thread's test context, or {@code null} if not set.
	 */
	public static TestContext getContext() {
		return testContext.get();
	}

	/**
	 * Removes the {@link TestContext} for the current thread.
	 * <p>
	 * Use this after test execution to prevent memory leaks and ensure clean thread
	 * reuse.
	 */
	public static void removeContext() {
		TestContextManager.getContext().getReportContext().getLogger().log("TEST CONTEXTS REMOVED");
		testContext.remove();
	}

	public static TestLogger getLogger() {
		TestContext context = TestContextManager.getContext();
		if (context != null) {
			ReportContext reportContext = context.getReportContext();
			if (reportContext != null) {
				TestLogger logger = reportContext.getLogger();
				if (logger != null) {
					return logger;
				}
				throw new ContextException("TestLogger Not set unable to get logger");
			}
			throw new ContextException("ReportContext Not set unable to get logger");
		} else {
			throw new ContextException("TestContext Not set unable to get logger");
		}
	}

}
