package com.trgan.login;

import org.testng.annotations.Test;

import com.trgan.framework.engine.TestEngine;
import com.trgan.pages.login.LoginPage;

public class TC_01_Login extends TestEngine {

	@Test
	public void tc_01_login() {
		LoginPage lp = new LoginPage();
		createNode("Login");
		lp.loginToApplication();
		createNode("Login success");
		createNode("HomePage");
	}

}
