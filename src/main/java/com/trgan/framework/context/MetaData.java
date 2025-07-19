package com.trgan.framework.context;

import com.trgan.framework.enums.BrowserType;
import com.trgan.framework.enums.EnvironmentType;

/**
 * class to handle meta-data related to execution
 */
public class MetaData {
	private EnvironmentType environment;
	private BrowserType browser;
	private String buildId;
	private String testGroup;
	private String testClassName;

	public MetaData(BrowserType browser, EnvironmentType environment, String buildId, String testGroup,
			String testClassName) {
		this.browser = browser;
		this.environment = environment;
		this.buildId = buildId;
		this.testGroup = testGroup;
		this.testClassName = testClassName;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public String getBuildId() {
		return buildId;
	}

	public EnvironmentType getEnvironment() {
		return environment;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public String getTestGroup() {
		return testGroup;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public void setEnvironment(EnvironmentType environment) {
		this.environment = environment;
	}

	public void setTestClassName(String testClassName) {
		this.testClassName = testClassName;
	}

	public void setTestGroup(String testGroup) {
		this.testGroup = testGroup;
	}

	@Override
	public String toString() {
		return String.format("MetaData[browser = %s, env = %s, build = %s, group = %s, testClassName = %s]", browser,
				environment, buildId, testGroup, testClassName);
	}
}
