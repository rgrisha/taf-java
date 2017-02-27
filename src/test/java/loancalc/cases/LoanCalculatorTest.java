package loancalc.cases;

import loancalc.base.tests.InternetBankTestBase;
import org.testng.annotations.Test;

/**
 * Created by ltrogr on 2/24/15.
 * Sample for creating tests
 * Swedbank public page loan calculator
 */
public class LoanCalculatorTest extends InternetBankTestBase {

  /*
  it is possible to set config file name if needed
  if not set, class name will be used instead
  {
    setPropertyFileName("landing");
  }
  */

  @Test
  public void testLoanCalculator() {
    getMainPage(getStartPage())
          .closeCookieMessage()
          .goToLoans()
          .goToCalculators()
          .goToConsumerLoan()
          .setMonthlyIncome(getConfiguration().getSalaryAmount())
          .setRequestedAmount(getConfiguration().getEligibleAmount())
          //.setSpreadsheetWageToAccount()
          .clickCalculate()
          .getLoanMonthlyFee().failIfNotEqualsTo(getConfiguration().getExpectedLoanResult(),
              "Loan monthly fee is other than expected");

  }
}
