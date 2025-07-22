package com.trgan.framework.engine;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.trgan.base.ResultData;
import com.trgan.exceptions.ContextException;
import com.trgan.framework.actions.ActionEngineImpl;
import com.trgan.framework.config.EnvironmentProperties;
import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.context.ExcelContext;
import com.trgan.framework.context.MetaData;
import com.trgan.framework.context.TestContext;
import com.trgan.framework.context.TestContextManager;
import com.trgan.framework.factory.RetryProxyFactory;
import com.trgan.framework.interfaces.IActionEngine;
import com.trgan.framework.utils.ExcelReader;
import com.trgan.framework.utils.TestLogger;

/**
 * Core engine for performing user actions with retry support, logging, and
 * context-aware utilities.
 * <p>
 * Provides access to WebDriver, Logger, Excel and Properties. Supporting
 * parallel execution via thread-local contexts.
 */
public class ActionEngine {

	protected final IActionEngine action;
	protected final EnvironmentProperties executionProps = TestEngine.environmentProps;
	protected final TestLogger logger = TestContextManager.getLogger();
	protected final WebDriver driver = TestContextManager.getContext().getDriverContext().getDriver();
	protected final ResultData resultData = TestContextManager.getContext().getReportContext().getResultData();
	protected final MetaData metaData = TestContextManager.getContext().getMetaData();

	public ActionEngine() {
		int maxAttempts = FrameworkProperties.retryMaxAttempt();
		int delayMs = FrameworkProperties.retryDelayMs();

		this.action = RetryProxyFactory.createWithRetry(IActionEngine.class, new ActionEngineImpl(), maxAttempts,
				delayMs);
	}

	protected final void warning(String message) {
		TestContextManager.getContext().getReportContext().getNode().warning(message);
		TestContextManager.getLogger().log("[X] " + message);
	}

	/**
	 * Can be used to retrieve excel value mapped to the given key from the excel,
	 * This method is indended for Single sheet excel(single sheet containing all
	 * data for test execution).
	 * 
	 * @param key
	 * @return value for given key
	 */

	public final String getExcel(String key) {
		TestContext context = TestContextManager.getContext();
		if (context != null) {
			ExcelContext excelContext = context.getExcelContext();
			if (excelContext != null) {
				Map<String, String> mapper = excelContext.getAllData();
				String value = mapper.get(key);
				if (value == null) {
					throw new ContextException("No value for the given EXCEL key : " + key);
				}
				return value;
			}
			throw new ContextException("ExcelContext Not set unable to fetch excel data");
		} else {
			throw new ContextException("TestContext Not set unable to fetch excel data");
		}
	}

	/**
	 * Can be used to retrieve excel value mapped to the given key from the
	 * specified sheet in Excel. This method is indended for multi-sheet excel(sheet
	 * based data)s sheet.
	 * 
	 * @param key
	 * @return value for given key
	 */

	public final String getExcel(String sheet, String key) {
		TestContext context = TestContextManager.getContext();
		if (context != null) {
			ExcelContext excelContext = context.getExcelContext();
			if (excelContext != null) {
				Map<String, String> mapper = ExcelReader.getSheetData(excelContext.getWorkBook(), sheet, "FIELDS", key);
				String value = mapper.get(key);
				if (value == null) {
					throw new ContextException("No value for the given EXCEL key : " + key);
				}
				return value;
			}
			throw new ContextException("ExcelContext Not set unable to fetch excel data");
		} else {
			throw new ContextException("TestContext Not set unable to fetch excel data");
		}
	}

}
