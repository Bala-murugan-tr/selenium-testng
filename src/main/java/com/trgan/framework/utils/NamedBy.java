package com.trgan.framework.utils;

import org.openqa.selenium.By;

/**
 * A helper class that pairs a Selenium {@link By} locator with a readable name.
 * <p>
 * Useful for logging, and reporting associate UI elements with descriptive
 * labels.
 */

public final class NamedBy {
	public final By locator;
	public final String name;

	private NamedBy(By locator, String name) {
		this.locator = locator;
		this.name = name;
	}

	/**
	 * Pairs locator name and locator
	 * 
	 * @param name    - name of the locator for logging and identification
	 * @param locator - Selenium {@link By} locator
	 * @return NamedBy
	 */
	public static NamedBy of(String name, By locator) {
		return new NamedBy(locator, name);
	}

}
