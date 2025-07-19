package com.trgan.pages.home;

import com.trgan.base.TestBase;

public class HomePage extends TestBase {
	public void verifyTitle() {
		var title = getExcel("Title");
		action.verifyPageTitle(title);
	}

	public void verifyURL() {
		var url = getExcel("URL");
		action.verifyPageURL(url);
	}

}
