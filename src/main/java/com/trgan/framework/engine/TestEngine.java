package com.trgan.framework.engine;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.trgan.exceptions.ContextException;
import com.trgan.framework.config.EnvironmentProperties;
import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.context.DriverContext;
import com.trgan.framework.context.ExcelContext;
import com.trgan.framework.context.MetaData;
import com.trgan.framework.context.ReportContext;
import com.trgan.framework.context.TestContext;
import com.trgan.framework.context.TestContextManager;
import com.trgan.framework.enums.BrowserType;
import com.trgan.framework.factory.BrowserFactory;
import com.trgan.framework.utils.ExcelReader;
import com.trgan.framework.utils.TestLogger;

/**
 * Core test engine responsible for orchestrating suite and method-level
 * execution flows.
 * <p>
 * Handles environment setup, reporting, browser management, and result logging
 * across the entire test lifecycle. Integrates with {@link TestContextManager}
 * to support thread-safe parallel execution and structured reporting.
 */
public class TestEngine {

	public static EnvironmentProperties environmentProps;
	public static FrameworkProperties frameworkProps;

	private static ExtentReports consolidatedReport;

	/**
	 * Used to create a new test node in current extent-test for logging. On each
	 * new node creation a screenshot of current driver state will be attached to
	 * the previous node, if {@linkplain screenshotOnNodes } property is set to
	 * true.
	 * 
	 * @param title - title of the node
	 */

	public static void createNode(String title) {
		var node = TestContextManager.getContext().getReportContext();
		if (node.getNode() == null) {
			node.setNode(title);
		} else {
			if (FrameworkProperties.screenshotOnNodes()) {
				attachScreenshot(node.getNode());
			}
			node.setNode(title);
		}
	}

	/**
	 * Initializes consolidated report and loads environment-specific configuration.
	 */
	@Parameters({ "group" })
	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(String group, ITestContext suiteContext) {
		String suiteName = suiteContext.getSuite().getName();
		try {
			environmentProps = EnvironmentProperties.load("environment.properties");
			frameworkProps = FrameworkProperties.load("framework.properties");
			initConsolidatedReport(group, suiteName);
		} catch (Exception e) {
			System.err.println("TestEngine.beforeSuite() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.err.println(trace);
			throw e;
		}
	}

	/**
	 * Initializes context and Creates logger, report paths, metadata, reads data
	 * from Excel, and launches the browser.
	 */
	@Parameters({ "group" })
	@BeforeMethod
	public void setupMethod(String group, Method method) {
		try {
			// Initialize an empty TestContext and register to TestContextManager
			TestContext ctx = new TestContext(null, null, null, null);
			TestContextManager.setContext(ctx);
			var testClassName = method.getDeclaringClass().getSimpleName();
			var logger = createLogger(testClassName);
			logger.log("TESTCASE INITIATED: " + testClassName);
			var meta = createMetaData(group, testClassName);
			logger.log("METADATA : " + meta.toString());
			createReports(testClassName, logger);
			readExcel();
			var browser = FrameworkProperties.getExecutionBrowser();
			var driver = createDriver(browser);
			logger.log("BROWSER LAUNCHED : " + browser);
			var url = environmentProps.getUrl(environmentProps.getEnvName());
			driver.get(url);
			logger.log("URL LAUNCHED : " + url);
		} catch (Exception e) {
			System.err.println("TestEngine.setupMethod() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.err.println(trace);
			throw e;

		}
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownMethod(ITestResult result, Method method) {
		try {
			executionResult(result, method.getDeclaringClass().getSimpleName());
			flushReport();
			quitBrowser();
			TestContextManager.removeContext();
		} catch (Exception e) {
			System.out.println("TestEngine.tearDownMethod() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.out.println(trace);
			throw e;
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		flushGlobalReport();
		environmentProps = null;
	}

//---------------------------------------helper methods--------------------------------------------------------------------------------------//

	private void initConsolidatedReport(String group, String suiteName) {
		String dir = FrameworkProperties.getReportDir() + File.separator + suiteName + "_ConsolidatedReport.html";
		ExtentSparkReporter reporter = new ExtentSparkReporter(dir).viewConfigurer().viewOrder()
				.as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST }).apply();
		reporter.config().setReportName("TRGAN " + suiteName + " Suite Summary");

		consolidatedReport = new ExtentReports();
		consolidatedReport.attachReporter(reporter);
		consolidatedReport.setSystemInfo("Browser", FrameworkProperties.getExecutionBrowser().toString());
		consolidatedReport.setSystemInfo("Env", environmentProps.getEnvName().toString());
		consolidatedReport.setSystemInfo("Build Version", environmentProps.getAppVersion());
		consolidatedReport.setSystemInfo("Group", group);
	}

	private TestLogger createLogger(String testClassName) {
		return new TestLogger(testClassName);
	}

	private MetaData createMetaData(String group, String testClassName) {
		var browser = FrameworkProperties.getExecutionBrowser();
		var environment = environmentProps.getEnvName();
		var buildNmr = environmentProps.getAppVersion();
		MetaData meta = new MetaData(browser, environment, buildNmr, group, testClassName);
		TestContextManager.getContext().setMetaData(meta);
		return meta;
	}

	private void createReports(String testClassName, TestLogger logger) {
		var reportDir = FrameworkProperties.getReportDir() + File.separator + testClassName;
		var userPath = System.getProperty("user.dir");
		logger.log("REPORT PATH : " + userPath + File.separator + reportDir);
		var extentReportPath = reportDir + File.separator + "Extent_" + testClassName + ".html";
		var individualReporter = new ExtentSparkReporter(extentReportPath);
		individualReporter.config().thumbnailForBase64(true).setDocumentTitle("REPORT FOR : " + testClassName);
		var individualExtent = new ExtentReports();
		individualExtent.attachReporter(individualReporter);
		var test = individualExtent.createTest(testClassName);
		logger.log("EXTENT REPORT CREATED : " + userPath + File.separator + extentReportPath);
		var globalTest = createGlobalTest(testClassName);
		ReportContext reportCtx = new ReportContext(individualExtent, globalTest, test, logger, reportDir);
		TestContextManager.getContext().setReportContext(reportCtx);
	}

	private static synchronized ExtentTest createGlobalTest(String testName) {
		return consolidatedReport.createTest(testName);
	}

	private WebDriver createDriver(BrowserType browser) {
		var driver = BrowserFactory.getDriver(browser);
		DriverContext driverCtx = new DriverContext(driver);
		TestContextManager.getContext().setDriverContext(driverCtx);
		return driver;
	}

	private void readExcel() {
		var reader = new ExcelReader();
		var workbook = reader.getWorkBook();
		var keyColumn = FrameworkProperties.getDataKeyColumn();
		Map<String, String> testCaseData = reader.getFirstSheetData(keyColumn, this.getClass().getSimpleName());
		ExcelContext excelCtx = new ExcelContext(testCaseData, workbook);
		TestContextManager.getContext().setExcelContext(excelCtx);
	}

	private void executionResult(ITestResult result, String testClassName) {
		var reportPath = File.separator + testClassName + File.separator + "Extent_" + testClassName + ".html";
		try {
			var rtx = TestContextManager.getContext().getReportContext();
			var globalTest = rtx.getGlobalTest();
			var node = rtx.getNode();
			switch (result.getStatus()) {
			case ITestResult.SUCCESS:
				globalTest.log(Status.PASS, MarkupHelper.createLabel("<a href='." + reportPath
						+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
						+ testClassName + "</a>", ExtentColor.GREEN));
				if (FrameworkProperties.screenshotonSuccess()) {
					attachScreenshot(node);
				}
				break;
			case ITestResult.FAILURE:
				globalTest.log(Status.FAIL, MarkupHelper.createLabel("<a href='." + reportPath
						+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
						+ testClassName + "</a>", ExtentColor.RED));
				if (FrameworkProperties.screenshotonFailure()) {
					attachScreenshot(node);
				}
				break;
			case ITestResult.SKIP:
				globalTest.log(Status.SKIP, MarkupHelper.createLabel("<a href='." + reportPath
						+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
						+ testClassName + "</a>", ExtentColor.GREY));
				break;
			}
			if (result.getStatus() != ITestResult.SUCCESS) {
				Throwable root = ExceptionUtils.getRootCause(result.getThrowable());
				StringBuilder refined = new StringBuilder("❌ Exception Location:<br>");
				for (StackTraceElement el : root.getStackTrace()) {
					if (el.getClassName().startsWith("com.trgan")) {
						refined.append(String.format("↳ %s.%s():%d<br>", el.getClassName(), el.getMethodName(),
								el.getLineNumber()));
					}
				}
				var trace = refineStackTrace(root.getStackTrace());
				String exceptionName = root.getClass().getSimpleName();
				String exceptionMessage = root.getMessage().split("Build info")[0];
				System.out.println(exceptionName);
				System.err.println(exceptionMessage);
				System.err.println(trace);
				if (node != null) {
					node.fail(exceptionName + " | " + exceptionMessage + " | ");
				}
				globalTest.fail(exceptionName + " | " + exceptionMessage + " | " + trace);
				TestContextManager.getLogger().log("TEST RESULT UPDATED TO REPORTS");
			}
		} catch (Exception e) {
			System.out.println("executionResult() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.out.println(trace);
		}
	}

	/**
	 * Captures a screenshot from the current WebDriver instance and attaches it to
	 * the node.
	 *
	 * @param node The ExtentTest node to which the screenshot will be attached.
	 * @throws ContextException if the node is null or screenshot capture fails.
	 */
	private static void attachScreenshot(ExtentTest node) {
		if (node == null) {
			throw new ContextException("Node is null, please make sure 'createNode' function is called atleast once");
		}
		var driver = TestContextManager.getContext().getDriverContext().getDriver();
		try {
			String srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			node.addScreenCaptureFromBase64String(srcFile);
		} catch (Exception e) {
			node.warning("Attaching Screenshot failed: " + e.getMessage());
		}
	}

	private static String refineStackTrace(StackTraceElement[] stackTraceElements) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement trace : stackTraceElements) {
			if (trace.getClassName().contains("com.trgan")) {
				sb.append("at ").append(trace.getClassName()).append(".").append(trace.getMethodName()).append("(")
						.append(trace.getFileName()).append(":").append(trace.getLineNumber()).append(")").append("\n");
			}
		}
		return sb + "";
	}

	private void quitBrowser() {
		try {
			var driver = TestContextManager.getContext().getDriverContext().getDriver();
			if (driver != null) {
				driver.quit();
				driver = null;
			}
			TestContextManager.getLogger().log("BROWSER CLOSED");
		} catch (Exception e) {
			System.out.println("flushReport() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.out.println(trace);
		}
	}

	private void flushReport() {
		try {
			var report = TestContextManager.getContext().getReportContext().getIndividualExtent();
			if (report != null) {
				report.flush();
				report = null;
			}
			TestContextManager.getLogger().log("TESTCASE'S EXTENT REPORT FLUSHED");
		} catch (Exception e) {
			System.out.println("flushReport() Failed !!");
			var trace = refineStackTrace(e.getStackTrace());
			System.out.println(e.getMessage());
			System.out.println(trace);
		}
	}

	private void flushGlobalReport() {
		if (consolidatedReport != null) {
			consolidatedReport.flush();
		}
		consolidatedReport = null;
	}
}
