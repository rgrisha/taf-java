package loancalc.pages;

import loancalc.base.pages.BaseInternetBankPage;
import org.openqa.selenium.WebDriver;

public class CalculatorPage extends BaseInternetBankPage<CalculatorPage> {

    public CalculatorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void waitForPageToLoad() {

    }

    public ConsumerLoanPage goToConsumerLoan() {
        clickLinkLocatedByHrefPart("lt/spreadsheets/consumer");
        return createPage(ConsumerLoanPage.class);
    }
}
