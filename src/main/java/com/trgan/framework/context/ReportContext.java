package com.trgan.framework.context;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.trgan.framework.reporter.ResultData;
import com.trgan.framework.utils.TestLogger;

/**
 * class to handle report related contexts
 */
public class ReportContext {
	private ExtentTest globalTest;
	private ExtentTest globalTestnode;
	private ExtentReports individualExtent;
	private ExtentTest individualTest;
	private ExtentTest individualTestNode;
	private TestLogger logger;
	private String reportDir;
	private ResultData resultData;

	public ReportContext(ExtentReports individualExtent, ExtentTest globalTest, ExtentTest individualTest,
			TestLogger logger, String reportDir) {
		this.individualExtent = individualExtent;
		this.globalTest = globalTest;
		this.individualTest = individualTest;
		this.logger = logger;
		this.reportDir = reportDir;
		this.resultData = new ResultData();
	}

	public ExtentTest getGlobalTest() {
		return this.globalTest;
	}

	public ExtentTest getGlobalTestNode() {
		return this.globalTestnode;
	}

	public ExtentReports getIndividualExtent() {
		return this.individualExtent;
	}

	public ExtentTest getIndividualTest() {
		return this.individualTest;
	}

	public ExtentTest getIndividualTestNode() {
		return this.individualTestNode;
	}

	public TestLogger getLogger() {
		return this.logger;
	}

	public String getReportDir() {
		return this.reportDir;
	}

	public ResultData getResultData() {
		return this.resultData;
	}

	public void setGlobalTest(ExtentTest globalTest) {
		this.globalTest = globalTest;
	}

	public void setGlobalTestNode(String description) {
		this.globalTestnode = globalTest.createNode(description);
	}

	public void setIndividualExtent(ExtentReports individualExtent) {
		this.individualExtent = individualExtent;
	}

	public void setIndividualTest(ExtentTest individualTest) {
		this.individualTest = individualTest;
	}

	public void setIndividualTestNode(String description) {
		this.individualTestNode = individualTest.createNode(description);
	}

	public void setLogger(TestLogger logger) {
		this.logger = logger;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

}
