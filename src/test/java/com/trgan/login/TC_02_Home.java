package com.trgan.login;

import org.testng.annotations.Test;

import com.trgan.framework.engine.TestEngine;
import com.trgan.pages.login.LoginPage;

public class TC_02_Home extends TestEngine {

	@Test
	public void tc_02_home() {
		createNode("test login");
		LoginPage lp = new LoginPage();
		lp.logoutFromApplication();
		// lp.loginToApplication();
	}

}
