# 📄 RetryTransformer Documentation
## 🧠 Purpose
The `RetryTransformer` enables selective test reruns by dynamically injecting a `RetryAnalyzer` into failed TestNG test methods. It honors opt-out tags like noretry, supports centralized configuration, and is designed for resilient, CI-friendly test execution.

## 🔧 Class Declaration
```java

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
```

## 🚀 How It Works


|Step|	Action|
|----|---------|
|1️⃣	|TestNG invokes `transform()` during test suite initialization|
|2️⃣	|Checks if retrying is globally enabled via `FrameworkProperties.retryTest()`|
|3️⃣	|Skips retry if the test method has a `@Test(groups={"noretry"})` tag|
|4️⃣	|Otherwise sets `RetryAnalyzer` class for dynamic re-execution|

-----------
## 🔁 Retry Analyzer – Conditional Retry Logic
### 📄 Class: RetryAnalyzer
This component defines how many times a test should be retried, and under what conditions it should be skipped entirely. It works hand-in-hand with the RetryTransformer
```java
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
        if (cause instanceof ExcelException || 
            cause instanceof ConfigException || 
            cause instanceof ContextException) {
            return false;
        }

        result.setAttribute("retryCount", retryCount);
        return retryCount++ < maxRetryCount;
    }
}
```
### ✅ Highlights

|Feature	|Description|
|----------|-------------|
|🔢 Retry Count Tracking	|Uses retryCount++ < maxRetryCount for capped attempts|
|🧠 Smart Failure Filtering	|Skips retry if the failure is due to known environment/config exceptions|
|🏷 Retry Attribution	|Injects retryCount into test result attributes for reporting|
|🔄 Configurable Max Attempts	|Pulls maxRetryCount from FrameworkProperties.retryTestMaxAttempt()|

### 💡 Integration with RetryTransformer
Only tests not tagged with `noretry` and allowed by config will have RetryAnalyzer attached dynamically:
```java
if (retryTest && !groups.contains("noretry")) {
    annotation.setRetryAnalyzer(RetryAnalyzer.class);
}
```


