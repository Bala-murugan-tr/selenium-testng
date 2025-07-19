package com.trgan.framework.context;

import org.openqa.selenium.WebDriver;

public class DriverContext {
	private WebDriver driver;

	public DriverContext(WebDriver driver) { this.driver = driver; }

	public WebDriver getDriver() { return driver; }

	public void setDriver(WebDriver driver) { this.driver = driver; }
}
