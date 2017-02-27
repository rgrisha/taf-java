package loancalc.pages;

import loancalc.base.pages.BaseInternetBankPage;
import org.openqa.selenium.WebDriver;

public class ConsumerLoanPage extends BaseInternetBankPage<ConsumerLoanPage> {

    public ConsumerLoanPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void waitForPageToLoad() {

    }

    public ConsumerLoanPage setRequestedAmount(String requestedAmount) {
       $("input#SpreadsheetRequestedLoanAmount").clear().setValue(requestedAmount);
       return this;
    }

    public ConsumerLoanPage setMonthlyIncome(String incomeAmount) {
        $("input#SpreadsheetNetMonthlyIncome").clear().setValue(incomeAmount);
        return this;
    }

    public ConsumerLoanResultPage clickCalculate() {
        $("div.consumerBottom>button").click();
        return createPage(ConsumerLoanResultPage.class);
    }
}
