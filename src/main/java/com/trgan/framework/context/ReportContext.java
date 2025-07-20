package com.trgan.framework.context;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.trgan.base.ResultData;
import com.trgan.framework.utils.TestLogger;

/**
 * class to handle report related contexts
 */
public class ReportContext {
	private ExtentReports individualExtent;
	private ExtentTest globalTest;
	private ExtentTest individualTest;
	private ExtentTest node;
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

	public ExtentReports getIndividualExtent() {
		return this.individualExtent;
	}

	public void setIndividualExtent(ExtentReports individualExtent) {
		this.individualExtent = individualExtent;
	}

	public ExtentTest getGlobalTest() {
		return this.globalTest;
	}

	public void setGlobalTest(ExtentTest globalTest) {
		this.globalTest = globalTest;
	}

	public ExtentTest getIndividualTest() {
		return this.individualTest;
	}

	public void setIndividualTest(ExtentTest individualTest) {
		this.individualTest = individualTest;
	}

	public ExtentTest getNode() {
		return this.node;
	}

	public void setNode(String description) {
		this.node = individualTest.createNode(description);
	}

	public TestLogger getLogger() {
		return this.logger;
	}

	public void setLogger(TestLogger logger) {
		this.logger = logger;
	}

	public String getReportDir() {
		return this.reportDir;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	public ResultData getResultData() {
		return this.resultData;
	}

}
