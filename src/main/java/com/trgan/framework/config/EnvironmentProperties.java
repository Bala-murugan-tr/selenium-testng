package com.trgan.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.trgan.exceptions.ConfigException;
import com.trgan.framework.enums.EnvironmentType;

/**
 * class to load environment properties related to the execution application
 */
public final class EnvironmentProperties {

	private final Properties properties = new Properties();

	public EnvironmentProperties(String filenameWithExtension) throws IOException {
		InputStream fis = getClass().getClassLoader().getResourceAsStream(filenameWithExtension);
		if (fis == null) {
			throw new ConfigException("Config file not found: " + filenameWithExtension);
		}
		properties.load(fis);
	}

	/**
	 * Loads the property file from {@linkplain path src/main/resources} folder
	 * 
	 * @param filename - filename with extension eg: environment.properties
	 * @throws IOException
	 */
	public static EnvironmentProperties load(String filename) {
		try {
			return new EnvironmentProperties(filename);
		} catch (IOException e) {
			throw new ConfigException("Failed to load " + filename + " " + e.getMessage());
		}
	}

	public EnvironmentType getEnvName() {
		return EnvironmentType.valueOf(getProperty("env.name"));
	}

	public String getAppName() {
		return getProperty("app.name");
	}

	public String getAppVersion() {
		return getProperty("app.version");
	}

	public String getUrl(EnvironmentType env) {
		return getProperty((env + ".url").toLowerCase());
	}

	public String getUsername() {
		var user = getProperty("user");
		var key = (getEnvName().toString().toLowerCase() + ".user" + user + ".username").toLowerCase();
		return getProperty(key);
	}

	public String getPassword() {
		var user = getProperty("user");
		var key = (getEnvName().toString().toLowerCase() + ".user" + user + ".password").toLowerCase();

		return getProperty(key);
	}

	private String getProperty(String key) {
		String sys = System.getProperty(key);
		if (sys != null && !sys.isBlank()) {
			return sys;
		}

		String cfg = properties.getProperty(key);
		if (cfg != null && !cfg.isBlank()) {
			return cfg;
		}

		throw new ConfigException(key + " not provided");
	}

	private String getProperty(String key, String defaultValue) {
		String sys = System.getProperty(key);
		if (sys != null && !sys.isBlank()) {
			return sys;
		}

		String cfg = properties.getProperty(key);
		if (cfg != null && !cfg.isBlank()) {
			return cfg;
		}

		return defaultValue;
	}

	public String getExecutor() {
		return getProperty("executorName", "Automation Tester");
	}

}
