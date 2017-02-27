package loancalc.pages;

import core.matchers.PageStringMatcher;
import loancalc.base.pages.BaseInternetBankPage;
import org.openqa.selenium.WebDriver;

public class ConsumerLoanResultPage extends BaseInternetBankPage<ConsumerLoanResultPage> {

    public ConsumerLoanResultPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void waitForPageToLoad() {

    }

    public PageStringMatcher<ConsumerLoanResultPage> getLoanMonthlyFee() {
        //String loanResult = elementFactory.toFindByXPath("//*[@id=\"consumerResults\"]/div[1]/div/div[3]/div[3]/big").getValue();
        String loanResult = elementFactory.toFindByCssSelector("#consumerResults > div.consumerInner.clearfix > div > div:nth-child(3) > div:nth-child(2) > big").getValue();
        return new PageStringMatcher<>(this, loanResult);
    }

}
