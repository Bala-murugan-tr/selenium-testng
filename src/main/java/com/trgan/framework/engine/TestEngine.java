package com.trgan.framework.engine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

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
import com.trgan.base.HtmlBuilder;
import com.trgan.base.ResultStatus;
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
import com.trgan.framework.reporter.HtmlBuilder;
import com.trgan.framework.reporter.ResultStatus;
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

	public static HtmlBuilder html = new HtmlBuilder();

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
			var startTime = LocalTime.now();
			// Initialize an empty TestContext and register to TestContextManager
			TestContext ctx = new TestContext(null, null, null, null);
			TestContextManager.setContext(ctx);
			var testClassName = method.getDeclaringClass().getSimpleName();
			var logger = createLogger(testClassName);
			logger.log("TESTCASE INITIATED: " + testClassName);
			var meta = createMetaData(group, testClassName, startTime);
			logger.log("METADATA : " + meta.toString());
			createReports(group, testClassName, logger);
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
			attachReportData();
			executionResult(result, method.getDeclaringClass().getSimpleName());
			flushReport();
			quitBrowser();
			TestContextManager.getContext().getReportContext().getLogger().flush();
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
		ExtentSparkReporter reporter = new ExtentSparkReporter(dir)
				.viewConfigurer()
				.viewOrder()
				.as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST })
				.apply();
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

	private MetaData createMetaData(String group, String testClassName, LocalTime startTime) {
		var browser = FrameworkProperties.getExecutionBrowser();
		var environment = environmentProps.getEnvName();
		var buildNmr = environmentProps.getAppVersion();
		MetaData meta = new MetaData(browser, environment, buildNmr, group, testClassName, startTime);
		TestContextManager.getContext().setMetaData(meta);
		return meta;
	}

	private void createReports(String group, String testClassName, TestLogger logger) {
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
		reportCtx.getResultData().testCase = testClassName;
		reportCtx.getResultData().group = group;
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
			var individualTest = rtx.getIndividualTest();
			var node = rtx.getNode();
			if (result.getStatus() != ITestResult.SUCCESS) {
				Throwable root = ExceptionUtils.getRootCause(result.getThrowable());
				StringBuilder refined = new StringBuilder("❌ Exception Location:<br>");
				for (StackTraceElement el : root.getStackTrace()) {
					if (el.getClassName().startsWith("com.trgan")) {
						refined
								.append(String
										.format("↳ %s.%s():%d<br>", el.getClassName(), el.getMethodName(),
												el.getLineNumber()));
					}
				}
				var trace = refineStackTrace(root.getStackTrace());
				String exceptionName = root.getClass().getSimpleName();
				String exceptionMessage = root.getMessage().split("Build info")[0];
				System.out.println(exceptionName);
				System.err.println(exceptionMessage);
				System.err.println(trace);

				switch (result.getStatus()) {

				case ITestResult.FAILURE:
					attachStatus("FAIL");
					html.addIndividualReport(testClassName, reportPath, exceptionMessage, "FAIL");
					if (node != null) {
						node.fail(exceptionName + " | " + exceptionMessage + " | ");
					} else {
						individualTest.fail(exceptionName + " | " + exceptionMessage + " | ");
					}
					globalTest
							.fail(MarkupHelper
									.createLabel("<a href='." + reportPath
											+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
											+ testClassName + "</a>", ExtentColor.RED));
					if (FrameworkProperties.screenshotonFailure()) {
						attachScreenshot(node, Status.FAIL);
					}
					break;
				case ITestResult.SKIP:
					attachStatus("SKIP");
					html.addIndividualReport(testClassName, reportPath, exceptionMessage, "SKIP");
					if (node != null) {
						node.skip(exceptionName + " | " + exceptionMessage + " | ");
					} else {
						individualTest.skip(exceptionName + " | " + exceptionMessage + " | ");
					}
					globalTest
							.skip(MarkupHelper
									.createLabel("<a href='." + reportPath
											+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
											+ testClassName + "</a>", ExtentColor.GREY));
					if (FrameworkProperties.screenshotonFailure()) {
						attachScreenshot(node, Status.SKIP);
					}
					break;
				}

				TestContextManager.getLogger().log("TEST RESULT UPDATED TO REPORTS");
			} else {
				attachStatus("PASS");
				html.addIndividualReport(testClassName, reportPath, "", "PASS");
				globalTest
						.pass(MarkupHelper
								.createLabel("<a href='." + reportPath
										+ "' target='_blank' style='color:inherit; text-decoration:none;'>📄 View Detailed Report : "
										+ testClassName + "</a>", ExtentColor.GREEN));
				if (FrameworkProperties.screenshotonSuccess()) {
					attachScreenshot(node, Status.PASS);
				}
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
	private static void attachScreenshot(ExtentTest node, Status status) {

		if (node == null) {
			throw new ContextException("Node is null, please make sure 'createNode' function is called atleast once");
		}
		var driver = TestContextManager.getContext().getDriverContext().getDriver();
		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage original = ImageIO.read(src);
			double scaleFactor = 1.0; 

			int width = (int)(original.getWidth() * scaleFactor);
			int height = (int)(original.getHeight() * scaleFactor);


			BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resized.createGraphics();

			// Apply bilinear interpolation for smoother scaling
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawImage(original, 0, 0, width, height, null);
			g.dispose();

			// Convert to base64
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(resized, "jpg", baos); // Use "jpg" for smaller size
			String srcFile = Base64.getEncoder().encodeToString(baos.toByteArray());

			node.log(status, "Exceution Complete");
			node.addScreenCaptureFromBase64String(srcFile);
		} catch (Exception e) {
			node.warning("Attaching Screenshot failed: " + e.getMessage());
		}
	}

	private static void attachScreenshot(ExtentTest node) {
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
				sb
						.append("at ")
						.append(trace.getClassName())
						.append(".")
						.append(trace.getMethodName())
						.append("(")
						.append(trace.getFileName())
						.append(":")
						.append(trace.getLineNumber())
						.append(")")
						.append("\n");
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
		var executor = environmentProps.getExecutor();
		var mode = FrameworkProperties.getExecutionMode().toString();
		html.generate(executor, mode, FrameworkProperties.getReportDir() + File.separator + "custom-report.html");
	}

	private void attachStatus(String result) {
		var testName = TestContextManager.getContext().getMetaData().getTestClassName();
		var group = TestContextManager.getContext().getMetaData().getTestGroup();
		var startTime = TestContextManager.getContext().getMetaData().getStartTime();
		var endTime = LocalTime.now();
		Duration duration = Duration.between(startTime, endTime);
		long seconds = duration.getSeconds();
		String formattedDuration = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);

		var status = new ResultStatus();
		status.testName = testName;
		status.group = group;
		status.startTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		status.endTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		status.duration = formattedDuration;
		status.result = result;

		html.addStatus(status);
	}

	private void attachReportData() {
		var resultData = TestContextManager.getContext().getReportContext().getResultData();
		html.addData(resultData);
	}

}
