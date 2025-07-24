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

	static int quoteId = 12121;

	public void addResults() {
		quoteId++;
		resultData.quoteId = "" + quoteId;
		resultData.policyId = "44444";
		resultData.premium = "1200";
		resultData.date = "07-0-2025";
		resultData.time = "07:30 PM";
//
//		HtmlBuilder builder = new HtmlBuilder();
//
//		// set q1 data...
//
//		builder.addRecord(resultData);
//		builder.addRecord(resultData);
//
//		// Repeat for others...
//
//		builder.generateReport("target/custom-report.html");

	}

	public void logoutFromApplication() {
		resultData.quoteId = "77777";
		resultData.policyId = "33333";
		resultData.premium = "1990";
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
