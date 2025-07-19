package com.trgan.framework.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

import com.trgan.exceptions.ContextException;
import com.trgan.framework.enums.BrowserType;
import com.trgan.framework.enums.DataStrategy;
import com.trgan.framework.enums.ExecutionMode;

/**
 * Class to load environment properties related to the framework
 */
public final class FrameworkProperties {
	private static final Properties framework = new Properties();

//	static {
//		try (InputStream input = FrameworkProperties.class.getClassLoader()
//				.getResourceAsStream("framework.properties")) {
//			framework.load(input);
//		} catch (IOException ignored) {
//			System.out.println("no config framework.properties");
//		}
//	}

	public FrameworkProperties(String filenameWithExtension) throws IOException {
		InputStream fis = getClass().getClassLoader().getResourceAsStream(filenameWithExtension);
		if (fis == null) {
			throw new FileNotFoundException("Config file not found: " + filenameWithExtension);
		}
		framework.load(fis);
	}

	/**
	 * Loads the property file from {@linkplain path src/main/resources} folder
	 * 
	 * @param filename - filename with extension eg: framework.properties
	 * @throws IOException
	 */
	public static FrameworkProperties load(String filename) {
		try {
			return new FrameworkProperties(filename);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load " + filename + " " + e.getMessage());
		}
	}

//*****************************Folder Paths****************************************************************************//
	public static String getArtifactsDir() {
		var defaultPath = Paths.get(System.getProperty("user.dir"), "default_artifact").toString();
		return getProperty("artifact.dir", defaultPath);
	}

//	public static String getDriversDir() {return getArtifactsDir()+ File.separator + "drivers"; }
	public static String getReportDir() {
		return getArtifactsDir() + File.separator + "reports";
	}

	public static String getDownloadDir() {
		return getArtifactsDir() + File.separator + "downloads";
	}

//*****************************Data Control****************************************************************************//
	public static String getDataDir() {
		var defaultPath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "testdata").toString();
		return getProperty("data.dir", defaultPath);
	}

	public static DataStrategy getDataStrategy() {
		return DataStrategy.valueOf(getProperty("data.strategy").toUpperCase());
	}

	public static String getDataKeyColumn() {
		return getProperty("data.keyColumn", "FIELDS");
	}

//*****************************Execution Control***********************************************************************//
	public static ExecutionMode getExecutionMode() {
		return ExecutionMode.valueOf(getProperty("execution.mode").toUpperCase());
	}

	public static BrowserType getExecutionBrowser() {
		return BrowserType.valueOf(getProperty("execution.browser").toUpperCase());
	}

//*****************************Driver Configuration *******************************************************************//
	public static boolean isHeadless() {
		return Boolean.parseBoolean(System.getProperty("headless", framework.getProperty("headless", "false")));
	}

	public static boolean isIncognito() {
		return Boolean.parseBoolean(System.getProperty("incognito", framework.getProperty("incognito", "false")));
	}

//*****************************Retry Strategy *************************************************************************//
	public static int retryMaxAttempt() {
		return Integer
				.parseInt(System.getProperty("retry.maxAttempts", framework.getProperty("retry.maxAttempts", "2")));
	}

	public static int retryDelayMs() {
		return Integer.parseInt(System.getProperty("retry.delayMs", framework.getProperty("retry.delayMs", "500")));
	}

//*****************************Screenshot Options *********************************************************************//
	public static boolean screenshotOnNodes() {
		return Boolean.parseBoolean(
				System.getProperty("screenshot.onNodes", framework.getProperty("screenshot.onNodes", "false")));
	}

	public static boolean screenshotonSuccess() {
		return Boolean.parseBoolean(
				System.getProperty("screenshot.onSuccess", framework.getProperty("screenshot.onSuccess", "true")));
	}

	public static boolean screenshotonFailure() {
		return Boolean.parseBoolean(
				System.getProperty("screenshot.onFailure", framework.getProperty("screenshot.onFailure", "true")));
	}

//*****************************Grid Configuration *********************************************************************//
	public static URL getGridUrl() {
		var uri = getProperty("grid.url");
		try {
			return new URI(uri).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new ContextException("URL invalid :" + uri);
		}
	}

	private static String getProperty(String key) {
		String sys = System.getProperty(key);
		if (sys != null && !sys.isBlank()) {
			return sys.trim();
		}

		String cfg = framework.getProperty(key);
		if (cfg != null && !cfg.isBlank()) {
			return cfg.trim();
		}

		throw new IllegalArgumentException(key + " not set in Framework.properties");
	}

	private static String getProperty(String key, String defaultValue) {
		String sys = System.getProperty(key);
		if (sys != null && !sys.isBlank()) {
			return sys.trim();
		}

		String cfg = framework.getProperty(key);
		if (cfg != null && !cfg.isBlank()) {
			return cfg.trim();
		}
		return defaultValue;
	}
}
