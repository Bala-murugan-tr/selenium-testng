package com.trgan.framework.factory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.enums.BrowserType;

/**
 * Class to create Remote WebDriver instances for Grid execution.
 */
public final class GridDriverFactory {

	/**
	 * Sets up and returns a RemoteWebDriver instance connected to Selenium Grid.
	 *
	 * This method prepares browser-specific capabilities for Chrome, Firefox, and
	 * Edge, applies headless/incognito options, suppresses automation prompts, and
	 * connects to the Grid node defined in your configuration.
	 *
	 * @param browser T
	 * @return A configured RemoteWebDriver connected to the Grid hub.
	 * @throws MalformedURLException    If the Grid hub URL is invalid.
	 * @throws IllegalArgumentException If the browser type is unsupported.
	 */
	public static WebDriver createDriver(final BrowserType browser) {
		MutableCapabilities capabilities;

		switch (browser) {
		case CHROME:
			ChromeOptions chromeOptions = new ChromeOptions();

			Map<String, Object> chromePrefs = new HashMap<>();
			chromePrefs.put("download.default_directory", FrameworkProperties.getDownloadDir());
			chromePrefs.put("plugins.always_open_pdf_externally", true);
			chromePrefs.put("profile.default_content_setting_values.notifications", 2);
			chromePrefs.put("profile.default_content_setting_values.geolocation", 2);
			chromeOptions.setExperimentalOption("prefs", chromePrefs);
			chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
			chromeOptions.setExperimentalOption("useAutomationExtension", false);

			if (FrameworkProperties.isHeadless()) {
				chromeOptions.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu",
						"--disable-dev-shm-usage", "--disable-software-rasterizer");
			} else {
				chromeOptions.addArguments("--start-maximized");
			}
			if (FrameworkProperties.isIncognito()) {
				chromeOptions.addArguments("--incognito");
			}

			chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
			capabilities = chromeOptions;
			break;

		case FIREFOX:
			FirefoxOptions firefoxOptions = new FirefoxOptions();
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
			capabilities = firefoxOptions;
			break;

		case EDGE:
			EdgeOptions edgeOptions = new EdgeOptions();

			Map<String, Object> edgePrefs = new HashMap<>();
			edgePrefs.put("download.default_directory", FrameworkProperties.getDownloadDir());
			edgePrefs.put("plugins.always_open_pdf_externally", true);
			edgePrefs.put("profile.default_content_setting_values.notifications", 2);
			edgePrefs.put("profile.default_content_setting_values.geolocation", 2);
			edgeOptions.setExperimentalOption("prefs", edgePrefs);
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
			edgeOptions.addArguments("--remote-allow-origins=*");
			capabilities = edgeOptions;
			break;

		default:
			throw new IllegalArgumentException("Unsupported browser type: " + browser);
		}

		var url = FrameworkProperties.getGridUrl();
		return new RemoteWebDriver(url, capabilities);

	}
}
