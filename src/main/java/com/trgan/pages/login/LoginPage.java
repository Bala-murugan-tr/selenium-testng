package com.trgan.pages.login;

import org.openqa.selenium.By;

import com.trgan.base.TestBase;
import com.trgan.framework.utils.NamedBy;

public class LoginPage extends TestBase {
	private static final NamedBy txtUserName = NamedBy.of("txtUserName", By.id("email"));
	private static final NamedBy txtPassword = NamedBy.of("txtPassword", By.id("pass"));
	private static final NamedBy btnLogin = NamedBy.of("btnLogin", By.name("login"));

	public void loginToApplication() {
		var pageTitle = getExcel("Title");
		action.verifyPageTitle(pageTitle);
		var pageUrl = getExcel("Url");
		action.verifyPageURLEndswith(pageUrl);

		String userName = executionProps.getUsername();
		String password = executionProps.getPassword();

		action.typeText(txtUserName, userName);
		driver.findElement(txtPassword.locator).sendKeys(password);
		logger.log("Entered [Password] to field [Password]");

		action.click(btnLogin);
		action.saveScreenshotLocally("LoginSuccess");

		logger.log("login successful");
	}

	public void logoutFromApplication() {
	}
}
