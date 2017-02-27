package loancalc.pages;

import loancalc.base.pages.BaseInternetBankPage;
import org.openqa.selenium.WebDriver;

public class MainPage extends BaseInternetBankPage<MainPage> {

	public MainPage(WebDriver driver) {
		super(driver);
	}

	public MainPage doWait() {
		waitForPageToLoad();
		return this;
	}

	@Override
	protected void waitForPageToLoad() {
		$("#FilterTextForm").isDisplayed();
	}
}