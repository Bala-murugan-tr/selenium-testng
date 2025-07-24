package com.trgan.framework.interfaces;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.trgan.framework.utils.NamedBy;

public interface IActionEngine {

	WebElement waitForElementToBeClickable(By nby);

	WebElement waitForElement(By nby);

	// *****************ELEMENT HIGHLIGHTS******************************//
	void highlight(NamedBy nby);

	void highlight(WebElement element);

	void blink(NamedBy nby);

	void blink(WebElement element);

	// *****************ZOOMING*****************************************//
	void zoomTo(double percent);

	void resetZoom();

	// *****************CHECKBOXS***************************************//
	// *****************RADIO BUTTONS***********************************//
	// *****************SCREENSHOTS*************************************//
	void saveScreenshot();

	void saveScreenshotLocally(String... imgName);

	// *****************CLICK ACTIONS***********************************//
	void click(NamedBy nby);

	void clickJS(NamedBy nby);

	// *****************MOUSE ACTIONS***********************************//
	void mouseClick(NamedBy nby);

	void doubleClick(NamedBy nby);

	void hover(NamedBy nby);

	void scrollTo(NamedBy nby);

	void dragAndDrop(NamedBy nby);

	// *****************TEXT INPUTS*************************************//
	void keyboardClear(NamedBy nby);

	void clearText(NamedBy nby);

	void clearValue(NamedBy nby);

	void typeText(NamedBy nby, String value);

	void clearAndTypeText(NamedBy nby, String value);

	void typeTextAndPressKeys(NamedBy nby, String value, Keys... keys);

	// *****************ELEMENT VALIDATIONS*****************************//
	boolean trueVisible(NamedBy nby);

	boolean elementPresent(NamedBy nby);

	boolean elementVisible(NamedBy nby);

	boolean elementEnabled(NamedBy nby);

	boolean elementDisplayed(NamedBy nby);

	// *****************TITLE VALIDATIONS*******************************//
	void verifyPageTitle(String expectedTitle);

	void verifyPageTitleStartswith(String expectedTitlePrefix);

	void verifyPageTitleEndswith(String expectedTitleSuffix);

	void verifyPageTitleContains(String title);

	// *****************URL VALIDATIONS*********************************//
	void verifyPageURL(String expectedURL);

	void verifyPageURLStartswith(String expectedURLPrefix);

	void verifyPageURLEndswith(String expectedURLSuffix);

	void verfifyPageURLContains(String url);

	// *****************FRAME METHODS***********************************//
	void toFrame(NamedBy nby);

	void toFrame(String frameName);

	void toFrame(int index);

	void mainFrame();

	void parentFrame();

	// *****************ALERT METHODS***********************************//
	void acceptAlert();

	void rejectAlert();

	void typeToAlert(String txt);

	// *****************WINDHOW HANDLES*********************************//
	void switchTab(String title);

	void switchTabUsingWinHandle(String handle);

	String createNewTab();

	String createNewWindow();

	void close(String handleId);

	// *****************DROPDOWNS***************************************//
	public void verifyMatchingTextList(NamedBy nby, String[] txts);

	public List<WebElement> waitForElements(NamedBy nby);

	void selectDropdown(NamedBy nby, String value);

}
