package com.trgan.framework.actions;

import java.util.ArrayList;

import com.trgan.framework.context.TestContextManager;

public class RetryHandler {

	public static <T> T retry(RetryableOperation<T> operation, int maxAttempts, int delayMs, String actionDescription)
			throws Exception {
		int attempt = 0;
		Throwable lastThrowable = null;

		while (attempt < maxAttempts) {
			try {
				T result = operation.perform(); // executes the Action
				return result;
			} catch (Throwable t) {
				lastThrowable = t;
				attempt++;
				if (attempt < maxAttempts) {
					log("🔁 " + actionDescription + " failed on attempt " + attempt + ": " + t.getCause());
					sleep(delayMs);
				} else {
					log("❌ " + actionDescription + " failed after " + maxAttempts + " attempts" + " - "
							+ lastThrowable.getCause());

				}
			}
		}
		var exception = lastThrowable.getCause();
		exception.setStackTrace(refineStackTrace(exception));
		throw new RuntimeException("Retry failed for action: " + actionDescription, exception);
	}

	private static StackTraceElement[] refineStackTrace(Throwable throwable) {
		var traces = throwable.getStackTrace();
		ArrayList<StackTraceElement> ee = new ArrayList<StackTraceElement>();
		for (StackTraceElement trace : traces) {
			if (trace.getClassName().contains("com.trgan")) {
				ee
						.add(new StackTraceElement(trace.getClassName(), trace.getMethodName(), trace.getFileName(),
								trace.getLineNumber()));
			}
		}
		return ee.toArray(new StackTraceElement[0]);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void log(String message) {
		var ctx = TestContextManager.getContext().getReportContext();
		message = message.split("Build info")[0];
		ctx.getLogger().log(message);
	}

	@FunctionalInterface
	public interface RetryableOperation<T> {
		T perform() throws Throwable;
	}
}
