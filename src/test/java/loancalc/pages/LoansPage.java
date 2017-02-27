package loancalc.pages;

import loancalc.base.pages.BaseInternetBankPage;
import org.openqa.selenium.WebDriver;

/**
 * Created by rolandas on 2/27/17.
 */
public class LoansPage extends BaseInternetBankPage<LoansPage> {

  public LoansPage(WebDriver driver) {
    super(driver);
  }

  @Override
  protected void waitForPageToLoad() {
    $("a[href*='susidurus_su_paskolos_grazinimo_sunkumais']").exists();
  }

  public CalculatorPage goToCalculators() {
    $("li>a[href*='/skaiciuokles']").setName("Skaičiuoklės").click();
    return createPage(CalculatorPage.class);
  }

}
