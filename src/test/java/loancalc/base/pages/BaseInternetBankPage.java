package loancalc.base.pages;

import core.page.AbstractWebPage;
import loancalc.pages.LoansPage;
import org.openqa.selenium.WebDriver;

public abstract class BaseInternetBankPage<T extends AbstractWebPage<T>> extends AbstractWebPage<T> {

	public BaseInternetBankPage(WebDriver driver) {
		super(driver);
	}

  public LoansPage goToLoans() {
		$("a[href='lt/pages/privatiems/paskolos']").click();
		return createPage(LoansPage.class);
	}

	public BaseInternetBankPage closeCookieMessage() {
		$("a.button.close-cookie-message").click();
		return this;
	}

}
