package com.trgan.framework.interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.trgan.framework.utils.NamedBy;

public interface IActionEngine {
	void typeText(NamedBy nby, String value);

	void clearAndTypeText(NamedBy nby, String value);

	void typeTextAndPressKeys(NamedBy nby, String value, Keys... keys);

	void click(NamedBy nby);

	void clickJS(NamedBy nby);

	WebElement waitForElementToBeClickable(By nby);

	WebElement waitForElement(By nby);

	void saveScreenshot();

	public void saveScreenshotLocally(String... imgName);

	void keyboardClear(NamedBy nby);

	void mouseClick(NamedBy nby);

	void doubleClick(NamedBy nby);

	void hover(NamedBy nby);

	void clearText(NamedBy nby);

	void clearValue(NamedBy nby);

	void verifyPageTitle(String expectedTitle);

	void verifyPageTitleStartswith(String expectedTitlePrefix);

	void verifyPageTitleEndswith(String expectedTitleSuffix);

	void verifyPageURL(String expectedURL);

	void verifyPageURLStartswith(String expectedURLPrefix);

	void verifyPageURLEndswith(String expectedURLSuffix);
}
