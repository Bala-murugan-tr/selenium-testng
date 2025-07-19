package com.trgan.framework.factory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.enums.BrowserType;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Class to create local driver instances.
 */
public final class LocalDriverFactory {
	/**
	 * Sets up and returns a Selenium WebDriver based on the specified browser.
	 * Supported browsers include Chrome, Firefox, and Edge.
	 * <p>
	 *
	 * This method automatically applies browser-specific configurations such as:
	 * custom download directories, headless and incognito modes, and suppression of
	 * popups, notifications, and location prompts. It also includes performance
	 * optimizations to ensure faster and more stable test execution. *
	 * 
	 * @param browser
	 * @return WebDriver
	 * @throws IllegalArgumentException If the browser type isn't supported.
	 */

	public static WebDriver createDriver(BrowserType browser) {
		WebDriver driver = null;
		try {
			switch (browser) {
			case CHROME:
//				System.setProperty("webdriver.chrome.silentOutput", "true");
//				System.setProperty("webdriver.chrome.verboseLogging", "false");
//				System.setProperty("webdriver.chrome.readableTimestamp", "true");
//				System.setProperty("webdriver.chrome.appendLog", "true");
//				System.setProperty("webdriver.chrome.logLevel", "INFO");

				ChromeOptions options = new ChromeOptions();

				// Suppress unwanted prompts and auto-download behavior
				Map<String, Object> prefs = new HashMap<>();
				prefs.put("download.default_directory", FrameworkProperties.getDownloadDir());
				prefs.put("plugins.always_open_pdf_externally", true);
				prefs.put("profile.default_content_setting_values.notifications", 2);
				prefs.put("profile.default_content_setting_values.geolocation", 2);
				options.setExperimentalOption("prefs", prefs);

				// Suppress "Chrome is being controlled" info bar and automation extension
				options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
				options.setExperimentalOption("useAutomationExtension", false);

				// Configure headless and incognito modes based on user-defined flags
				if (FrameworkProperties.isHeadless()) {
					options.addArguments("--headless=new", "--window-size=1920,1080");
					options.addArguments("--disable-gpu", "--disable-dev-shm-usage", "--disable-software-rasterizer");
				} else {
					options.addArguments("--start-maximized");
				}

				if (FrameworkProperties.isIncognito()) {
					options.addArguments("--incognito");
				}

				options.setPageLoadStrategy(PageLoadStrategy.EAGER);

				// Disable unnecessary features to improve performance and reduce noise
				options.addArguments("--disable-features=Translate,MediaRouter,VizDisplayCompositor",
						"--disable-notifications", "--disable-popup-blocking", "--disable-extensions",
						"--disable-background-networking", "--disable-sync", "--disable-default-apps");

				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver(options);
				break;

			case FIREFOX:
				FirefoxOptions firefoxOptions = new FirefoxOptions();

				// Set Firefox preferences similar to Chrome for consistency
				firefoxOptions.addPreference("browser.download.folderList", 2);
				firefoxOptions.addPreference("browser.download.dir", FrameworkProperties.getDownloadDir());
				firefoxOptions.addPreference("pdfjs.disabled", true);
				firefoxOptions.addPreference("geo.enabled", false);
				firefoxOptions.addPreference("dom.webnotifications.enabled", false);

				if (FrameworkProperties.isHeadless()) {
					firefoxOptions.addArguments("--headless");
				} else {
					firefoxOptions.addArguments("--start-maximized");
				}

				if (FrameworkProperties.isIncognito()) {
					firefoxOptions.addArguments("-private");
				}

				firefoxOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

				WebDriverManager.firefoxdriver().clearDriverCache().setup();
				driver = new FirefoxDriver(firefoxOptions);
				break;

			case EDGE:
				EdgeOptions edgeOptions = new EdgeOptions();

				// Replicate preference setup from Chrome
				Map<String, Object> edgePrefs = new HashMap<>();
				edgePrefs.put("download.default_directory", FrameworkProperties.getDownloadDir());
				edgePrefs.put("plugins.always_open_pdf_externally", true);
				edgePrefs.put("profile.default_content_setting_values.notifications", 2);
				edgePrefs.put("profile.default_content_setting_values.geolocation", 2);
				edgeOptions.setExperimentalOption("prefs", edgePrefs);

				// Suppress automation artifacts
				edgeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
				edgeOptions.setExperimentalOption("useAutomationExtension", false);

				if (FrameworkProperties.isHeadless()) {
					edgeOptions.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu",
							"--disable-dev-shm-usage", "--disable-software-rasterizer");
				} else {
					edgeOptions.addArguments("--start-maximized");
				}

				if (FrameworkProperties.isIncognito()) {
					edgeOptions.addArguments("--inprivate");
				}

				edgeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

				// Disable features and allow remote origin connections
				edgeOptions.addArguments("--disable-features=Translate,MediaRouter,VizDisplayCompositor",
						"--disable-notifications", "--disable-popup-blocking", "--disable-extensions",
						"--disable-background-networking", "--disable-default-apps", "--remote-allow-origins=*");

				WebDriverManager.edgedriver().setup();
				driver = new EdgeDriver(edgeOptions);
				break;

			default:
				throw new IllegalArgumentException("Unsupported browser type: " + browser);
			}

			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		} catch (Exception e) {
			throw new RuntimeException("Driver initialization failed for browser: " + browser, e);
		}
		return driver;
	}

}
