package com.trgan.framework.actions;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.trgan.framework.context.TestContextManager;
import com.trgan.framework.interfaces.IActionEngine;
import com.trgan.framework.utils.NamedBy;

public class ActionEngineImpl implements IActionEngine {
	@Override
	public void typeTextAndPressKeys(NamedBy nby, String value, Keys... keys) {
//		keys.name()
		var ele = waitForElement(nby.locator);
		ele.sendKeys(value);
		for (Keys key : keys) {
			ele.sendKeys(key);
		}
		log("Entered [" + value + "] to field [" + nby.name + "] and pressed [" + keys.toString() + "]");
	}

	// *****************ELEMENT HIGHLIGHTS*****************************//
	// *****************ELEMENT VALIDATIONS*****************************//
	// *****************TITLE VALIDATIONS*****************************//
	// *****************URL VALIDATIONS*****************************//
	// *****************TEXT VALIDATIONS*****************************//
	// *****************TEXT INPUTS*****************************//
	// *****************CLICK ACTIONS*****************************//
	// *****************SCROLL ACTIONS*****************************//
	// *****************DROPDOWNS*****************************//
	// *****************SCREENSHOTS*****************************//
	// *****************MOUSE ACTIONS*****************************//
	// *****************PRIVATE METHODS*****************************//
	private void log(String message) {
		TestContextManager.getContext().getReportContext().getNode().pass(message);
		TestContextManager.getContext().getReportContext().getLogger().log(message);
	}

	private WebDriver driver() {
		return TestContextManager.getContext().getDriverContext().getDriver();
	}

	@Override
	public void verifyPageTitle(String expectedTitle) {
		var pageTitle = driver().getTitle();
		Assert.assertEquals(pageTitle, expectedTitle,
				String.format("Page title verification failed !", expectedTitle, pageTitle));
		log(String.format("Page title verified exepected & found [[%s]]", pageTitle));
	}

	@Override
	public void verifyPageTitleStartswith(String expectedTitlePrefix) {
		var pageTitle = driver().getTitle();
		Assert.assertTrue(pageTitle.startsWith(expectedTitlePrefix),
				String.format("Title [%s] does not start with [%s]", pageTitle, expectedTitlePrefix));
	}

	@Override
	public void verifyPageTitleEndswith(String expectedTitleSuffix) {
		var pageTitle = driver().getTitle();
		Assert.assertTrue(pageTitle.endsWith(expectedTitleSuffix),
				String.format("Title [%s] does not end with [%s]", pageTitle, expectedTitleSuffix));
	}

	@Override
	public WebElement waitForElementToBeClickable(By by) {
		WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(20));
		return wait.until(ExpectedConditions.elementToBeClickable(by));
	}

	@Override
	public WebElement waitForElement(By by) {
		WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(20));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	@Override
	public void saveScreenshot() {
		try {
		File src = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);
		BufferedImage original;
			original = ImageIO.read(src);
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
		TestContextManager.getContext().getReportContext().getIndividualTest()
				.addScreenCaptureFromBase64String(srcFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveScreenshotLocally(String... imgName) {
		var reportDir = TestContextManager.getContext().getReportContext().getReportDir();
		var name = (imgName.length > 0) ? imgName[0] : "Screenshot";
		try {
			File scrnSht = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);
			var screenshotDir = Paths.get(reportDir);
			var screenshotPath = screenshotDir.resolve(name + ".png");
//			.resolve(name + "_" + new SimpleDateFormat("HHmmssSSS").format(new Date()) + ".png");
			Files.copy(scrnSht.toPath(), screenshotPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	@Override
	public void typeText(NamedBy nby, String value) {
		waitForElement(nby.locator).sendKeys(value);
		log("Entered [" + value + "] to field [" + nby.name + "]");
	}

	@Override
	public void clearText(NamedBy nby) {
		waitForElement(nby.locator).clear();
		log("Cleared field [" + nby.name + "]");
	}

	@Override
	public void clearValue(NamedBy nby) {
		waitForElement(nby.locator).getAttribute("value");
		/**/
		log("Cleared value-attribute to field [" + nby.name + "]");
	}

	@Override
	public void clearAndTypeText(NamedBy nby, String value) {
		var ele = waitForElement(nby.locator);
		ele.clear();
		ele.sendKeys(value);
		log("Cleared & Entered [" + value + "] to field [" + nby.name + "]");
	}

	@Override
	public void keyboardClear(NamedBy nby) {
		var ele = waitForElement(nby.locator);
		Actions act = new Actions(driver());
		act.keyDown(ele, Keys.LEFT_CONTROL).sendKeys("a").keyDown(Keys.BACK_SPACE).perform();
	}

	@Override
	public void click(NamedBy nby) {
		waitForElement(nby.locator);
		waitForElementToBeClickable(nby.locator).click();
		log("Clicked [" + nby.name + "]");

	}

	@Override
	public void clickJS(NamedBy nby) {
		var ele = waitForElementToBeClickable(nby.locator);
		var js = (JavascriptExecutor) driver();
		js.executeScript("argument[0].", ele);
		log("Clicked [" + nby.name + "] using JavaScript");
	}

	@Override
	public void mouseClick(NamedBy nby) {
		var ele = waitForElementToBeClickable(nby.locator);
		Actions act = new Actions(driver());
		act.click(ele);
		log("Clicked [" + nby.name + "] using Mouse");
	}

	@Override
	public void doubleClick(NamedBy nby) {
		var ele = waitForElementToBeClickable(nby.locator);
		Actions act = new Actions(driver());
		act.doubleClick(ele);
		log("Double Clicked [" + nby.name + "] using Mouse");
	}

	@Override
	public void hover(NamedBy nby) {
		var ele = waitForElementToBeClickable(nby.locator);
		Actions act = new Actions(driver());
		act.moveToElement(ele).pause(Duration.ofMillis(500));
		log("Hovered to [" + nby.name + "] using Mouse");
	}

	@Override
	public void verifyPageURL(String expectedURL) {
		var pageUrl = driver().getCurrentUrl();
		Assert.assertEquals(pageUrl, expectedURL, String.format("Page url verification failed !"));
		log(String.format("Page url verified exepected & found [[%s]]", pageUrl));
	}

	public void selectDropdown(NamedBy nby, String value) {
		var ele = waitForElement(nby.locator);
		Select sc = new Select(ele);
		sc.selectByValue(null);
	}

	@Override
	public void verifyPageURLStartswith(String expectedURLPrefix) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyPageURLEndswith(String expectedURLSuffix) {
		// TODO Auto-generated method stub

	}

}
