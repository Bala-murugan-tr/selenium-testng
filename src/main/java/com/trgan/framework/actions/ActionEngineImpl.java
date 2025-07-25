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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.context.TestContextManager;
import com.trgan.framework.interfaces.IActionEngine;
import com.trgan.framework.utils.NamedBy;

public final class ActionEngineImpl implements IActionEngine {
	private static final int wait = FrameworkProperties.getWait();
	private static final int shortWait = FrameworkProperties.shortWait();
	private static final int longWait = FrameworkProperties.longWait();

	// *****************ELEMENT HIGHLIGHTS******************************//
	@Override
	public void highlight(NamedBy nby) {
		// TODO Auto-generated method stub

	}

	@Override
	public void highlight(WebElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blink(NamedBy nby) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blink(WebElement element) {
		// TODO Auto-generated method stub

	}
	// *****************TEXT VALIDATIONS********************************//

	// *****************SCROLL ACTIONS**********************************//

	// *****************FRAME METHODS***********************************//
	@Override
	public void toFrame(int index) {
		// zero-based index
		driver().switchTo().frame(index);
		log("Switched to frame with index[" + index + "]");
	}

	@Override
	public void toFrame(String frameName) {
		driver().switchTo().frame(frameName);
		log("Switched to frame with frame name[" + frameName + "]");
	}

	@Override
	public void toFrame(NamedBy nby) {
		var frameElement = waitForElement(nby.locator);
		driver().switchTo().frame(frameElement);
		log("Switched to frame [" + nby.name + "]");
	}

	@Override
	public void mainFrame() {
		driver().switchTo().defaultContent();
		log("Switched to first/main frame");
	}

	@Override
	public void parentFrame() {
		driver().switchTo().parentFrame();
		log("Switched to parent frame");
	}

	// *****************ALERT METHODS***********************************//
	@Override
	public void acceptAlert() {
		var text = driver().switchTo().alert().getText();
		driver().switchTo().alert().accept();
		log("Alert accepted : [" + text + "]");
	}

	@Override
	public void rejectAlert() {
		var text = driver().switchTo().alert().getText();
		driver().switchTo().alert().dismiss();
		log("Alert rejected : [" + text + "]");
	}

	@Override
	public void typeToAlert(String txt) {
		var text = driver().switchTo().alert().getText();
		driver().switchTo().alert().sendKeys(text);
		log("Entered [" + txt + "] to Alert containing text [" + text + "]");
	}

	// *****************WINDHOW HANDLES*********************************//
	@Override
	public void switchTab(String title) {
		var tabs = driver().getWindowHandles();
		var flag = false;
		for (String tab : tabs) {
			driver().switchTo().window(tab);
			var actualTitle = driver().getTitle();
			if (actualTitle.equals(title)) {
				flag = true;
				log("Switched to tab with title [" + title + "]");
				return;
			}
		}
		Assert.assertTrue(flag, "Switch to tab with title [" + title + "] failed");
	}

	@Override
	public void switchTabUsingWinHandle(String handle) {
		driver().switchTo().window(handle);
		log("Switched to tab with window handle id [" + handle + "]");
	}

	@Override
	public String createNewTab() {
		var handleId = driver().switchTo().newWindow(WindowType.TAB).getWindowHandle();
		log("Created new tab [" + handleId + "]");
		return handleId;
	}

	@Override
	public String createNewWindow() {
		var handleId = driver().switchTo().newWindow(WindowType.WINDOW).getWindowHandle();
		log("Created new window [" + handleId + "]");
		return handleId;
	}

	@Override
	public void close(String handleId) {
		driver().switchTo().window(handleId).close();
		log("Closed tab [" + handleId + "]");
	}

	// *****************ZOOMING*****************************************//
	@Override
	public void zoomTo(double percent) {
		JavascriptExecutor js = (JavascriptExecutor) driver();
		js.executeScript("document.body.style.zoom='" + percent + "%'");
		log("Set page zoom to [" + percent + "]%");
	}

	@Override
	public void resetZoom() {
		JavascriptExecutor js = (JavascriptExecutor) driver();
		js.executeScript("document.body.style.zoom='" + 100 + "%'");
		log("Page zoom resetted");
	}

	// *****************PRIVATE METHODS*********************************//
	private void log(String message) {
		TestContextManager.getContext().getReportContext().getIndividualTestNode().pass(message);
		TestContextManager.getContext().getReportContext().getGlobalTestNode().pass(message);
		TestContextManager.getContext().getReportContext().getLogger().log(message);
	}

	private WebDriver driver() {
		return TestContextManager.getContext().getDriverContext().getDriver();
	}

	// *****************TITLE VALIDATIONS*******************************//
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
	public void verifyPageTitleContains(String title) {
		var pageTitle = driver().getTitle();
		Assert.assertEquals(pageTitle.contains(title),
				String.format("Page title verification failed !", title, pageTitle));
		log(String.format("Page title contains [[%s]]", title));
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

	// *****************SCREENSHOTS*************************************//
	@Override
	public void saveScreenshot() {
		try {
			File src = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);
			BufferedImage original;
			original = ImageIO.read(src);
			double scaleFactor = 1.0;

			int width = (int) (original.getWidth() * scaleFactor);
			int height = (int) (original.getHeight() * scaleFactor);

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

	// *****************TEXT INPUTS*************************************//
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

	// *****************ELEMENT VALIDATIONS*****************************//

	// *****************CLICK ACTIONS***********************************//
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

	// *****************MOUSE ACTIONS***********************************//
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
	public void scrollTo(NamedBy nby) {
		var ele = waitForElement(nby.locator);
		Actions act = new Actions(driver());
		act.moveToElement(ele);
		log("Moved to [" + nby.name + "] using Mouse");
	}

	// *****************URL VALIDATIONS*********************************//
	@Override
	public void verifyPageURL(String expectedURL) {
		var pageUrl = driver().getCurrentUrl();
		Assert.assertEquals(pageUrl, expectedURL, String.format("Page url verification failed !"));
		log(String.format("Page url verified exepected & found [[%s]]", pageUrl));
	}

	@Override
	public void verifyPageURLStartswith(String expectedURLPrefix) {
		var pageUrl = driver().getCurrentUrl();
		Assert.assertEquals(pageUrl.startsWith(expectedURLPrefix), String.format("Page url verification failed !"));
		log(String.format("Page url prefix verified exepected & found [[%s]]", expectedURLPrefix));
	}

	@Override
	public void verifyPageURLEndswith(String expectedURLSuffix) {
		var pageUrl = driver().getCurrentUrl();
		Assert.assertEquals(pageUrl.endsWith(expectedURLSuffix), String.format("Page url verification failed !"));
		log(String.format("Page url suffix verified exepected & found [[%s]]", expectedURLSuffix));
	}

	@Override
	public void verfifyPageURLContains(String url) {
		var pageUrl = driver().getCurrentUrl();
		Assert.assertEquals(pageUrl.contains(url), String.format("Page url verification failed !"));
		log(String.format("Page url contains [[%s]]", url));
	}

	@Override
	public List<WebElement> waitForElements(NamedBy nby) {
		var wait = new WebDriverWait(driver(), Duration.ofSeconds(20L));
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(nby.locator));
	}

	// *****************DROPDOWNS***************************************//
	@Override
	public void verifyMatchingTextList(NamedBy nby, String[] txts) {
		List<String> sortedTxts = Arrays.asList(txts);
		var elements = waitForElements(nby);
		List<String> eleTxts = new ArrayList<>();
		for (WebElement ele : elements) {
			eleTxts.add(ele.getText());
		}
		Assert.assertTrue(eleTxts.containsAll(sortedTxts),
				"One or More given text option is not present in [" + nby.name + "]");
		log(String.format("[%s] are present in List/Dropdown [%]", txts.toString(), nby.locator));
	}

	@Override
	public void selectDropdown(NamedBy nby, String value) {
		var ele = waitForElement(nby.locator);
		Select sc = new Select(ele);
		sc.selectByValue(null);
	}

	@Override
	public void dragAndDrop(NamedBy nby) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean elementPresent(NamedBy nby) {
		try {
			shortWait().until(ExpectedConditions.presenceOfElementLocated(nby.locator));
			log("Element [" + nby.name + "] presence: true");
			return true;
		} catch (Exception e) {
			log("Element [" + nby.name + "] presence: false");
			return false;
		}
	}

	@Override
	public boolean elementVisible(NamedBy nby) {
		try {
			shortWait().until(ExpectedConditions.visibilityOfElementLocated(nby.locator));
			log("Element [" + nby.name + "] visible: true");
			return true;
		} catch (Exception e) {
			log("Element [" + nby.name + "] visible: false");
			return false;
		}
	}

	@Override
	public boolean elementEnabled(NamedBy nby) {
		try {
			var flag = driver().findElement(nby.locator).isEnabled();
			log("Element [" + nby.name + "] enabled [" + flag + "]");
			return flag;
		} catch (Exception e) {
			log("Element [" + nby.name + "] enabled: false");
			return false;
		}
	}

	@Override
	public boolean elementDisplayed(NamedBy nby) {
		try {
			var flag = driver().findElement(nby.locator).isDisplayed();
			log("Element [" + nby.name + "] displayed: [" + flag + "]");
			return flag;
		} catch (Exception e) {
			log("Element [" + nby.name + "] displayed: false");
			return false;
		}
	}

	@Override
	public boolean trueVisible(NamedBy nby) {
		try {
			WebElement element = waitForElement(nby.locator);
			if (!element.isDisplayed()) {
				log("Element [" + nby.name + "] is not displayed.");
				return false;
			}
			if (isOutsideViewPort(element)) {
				log("Element [" + nby.name + "] is outside viewport. Scrolling into view.");
				((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView({block: 'center'});",
						element);
				Thread.sleep(300);
				if (isOutsideViewPort(element)) {
					log("Element [" + nby.name + "] still outside viewport after scroll.");
					return false;
				}
			}
			if (!isObscured(element)) {
				log("Element [" + nby.name + "] is overlapped by another element.");
				return false;
			}
			log("Element [" + nby.name + "] is truly visible.");
			return true;
		} catch (Exception e) {
			log("Error checking true visibility for [" + nby.name + "]: " + e.getMessage());
			return false;
		}
	}

	public boolean isObscured(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver();
		return !(Boolean) js.executeScript("var elem = arguments[0];" + "var rect = elem.getBoundingClientRect();"
				+ "var cx = rect.left + rect.width / 2;" + "var cy = rect.top + rect.height / 2;"
				+ "var topElem = document.elementFromPoint(cx, cy);" + "while (topElem) {"
				+ "  if (topElem === elem) return true;" + "  topElem = topElem.parentElement;" + "}" + "return false;",
				element);
	}

	public boolean isOutsideViewPort(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver();
		return (Boolean) js.executeScript("var elem = arguments[0];" + "var rect = elem.getBoundingClientRect();"
				+ "var viewHeight = Math.max(document.documentElement.clientHeight, window.innerHeight);"
				+ "var viewWidth = Math.max(document.documentElement.clientWidth, window.innerWidth);"
				+ "return (rect.bottom < 0 || rect.top > viewHeight || rect.right < 0 || rect.left > viewWidth);",
				element);
	}

	private WebDriverWait shortWait() {
		return new WebDriverWait(driver(), Duration.ofSeconds(shortWait));
	}

	private WebDriverWait getWait() {
		return new WebDriverWait(driver(), Duration.ofSeconds(shortWait));
	}

	private WebDriverWait longWait() {
		return new WebDriverWait(driver(), Duration.ofSeconds(shortWait));
	}

}
