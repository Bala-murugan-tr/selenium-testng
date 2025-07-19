package com.trgan.framework.factory;

import org.openqa.selenium.WebDriver;

import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.enums.BrowserType;
import com.trgan.framework.enums.ExecutionMode;

/**
 * A factory class for creating WebDriver instances based on the configured
 * execution mode.
 */

public final class BrowserFactory {
	/**
	 * Retrieves a WebDriver instance tailored to the selected execution mode
	 * (Local, Grid, or Cloud) and browser type (e.g., Chrome, Firefox, Edge).
	 *
	 * @param browser The browser type to initialize.
	 * @return A configured WebDriver instance suitable for the current execution
	 *         environment.
	 */

	public static WebDriver getDriver(BrowserType browser) {
		ExecutionMode mode = FrameworkProperties.getExecutionMode();
		switch (mode) {
		case LOCAL:
			return LocalDriverFactory.createDriver(browser);
		case GRID:
			return GridDriverFactory.createDriver(browser);
		default:
			return CloudDriverFactory.createDriver(browser);
		}
	}

}
