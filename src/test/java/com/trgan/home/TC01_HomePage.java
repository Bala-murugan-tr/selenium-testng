package com.trgan.home;

import org.testng.annotations.Test;

import com.trgan.framework.engine.TestEngine;
import com.trgan.pages.home.HomePage;

public class TC01_HomePage extends TestEngine {

	@Test
	public void tc_01() {
		TestEngine.createNode("HomePage Validations");
		HomePage hp = new HomePage();
		hp.verifyTitle();
		hp.verifyURL();
		TestEngine.createNode("Navigation-Bar Validations");
		

	}

}
