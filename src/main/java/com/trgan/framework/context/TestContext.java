package com.trgan.framework.context;

/**
 * Container class for storing all execution-specific contexts during a test
 * run.
 */
public class TestContext {
	private DriverContext driverContext;
	private ReportContext reportContext;
	private ExcelContext excelContext;
	private MetaData metaData;

	/**
	 * Container class for storing all execution-specific contexts during a test
	 * run.
	 * <p>
	 * This object must be registered with {@link TestContextManager} to enable
	 * thread-safe access and support parallel test execution.
	 *
	 * @param driverContext Provides browser/session-level control via WebDriver.
	 * @param reportContext Handles logging and reporting utilities for the current
	 *                      test.
	 * @param excelContext  Supplies data-driven execution support via Excel
	 *                      sources.
	 * @param metaData      Carries test metadata, such as environment details and
	 *                      annotations.
	 */

	public TestContext(DriverContext driverContext, ReportContext reportContext, ExcelContext excelContext,
			MetaData metaData) {
		this.driverContext = driverContext;
		this.reportContext = reportContext;
		this.excelContext = excelContext;
		this.metaData = metaData;
	}

	public DriverContext getDriverContext() {
		return this.driverContext;
	}

	public ExcelContext getExcelContext() {
		return this.excelContext;
	}

	public MetaData getMetaData() {
		return this.metaData;
	}

	public ReportContext getReportContext() {
		return this.reportContext;
	}

	public void setDriverContext(DriverContext driverContext) {
		this.driverContext = driverContext;
	}

	public void setExcelContext(ExcelContext excelContext) {
		this.excelContext = excelContext;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
}
