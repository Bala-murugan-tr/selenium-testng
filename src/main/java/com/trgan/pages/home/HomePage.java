package com.trgan.pages.home;

import org.openqa.selenium.By;

import com.trgan.base.TestBase;
import com.trgan.framework.utils.NamedBy;

public class HomePage extends TestBase {

	private final NamedBy navBar = NamedBy.of("navBar", By.xpath("//nav[@id='desktop-nav']/div/ul/li"));

	public void verifyTitle() {
		var title = getExcel("Title");
		action.verifyPageTitle(title);
	}

	public void verifyURL() {
		var url = getExcel("URL");
		action.verifyPageURL(url);
	}

	public void verifyPresenceofHeaderMenu() {
		var txtList = getExcel("Headers").split("||");
		action.verifyMatchingTextList(navBar,txtList);
	}

}
