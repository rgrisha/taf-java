package loancalc.base.tests;

import config.CountryFactoryImpl;
import config.EnvironmentFactoryImpl;
import core.test.AbstractTestCase;
import loancalc.config.LoanCalculatorConfiguration;
import loancalc.pages.MainPage;


public abstract class InternetBankTestBase extends AbstractTestCase<LoanCalculatorConfiguration> {

  public InternetBankTestBase() {
    super(new CountryFactoryImpl(), new EnvironmentFactoryImpl());
  }

  protected StartPage<MainPage> getStartPage() {
    return context -> {
      System.out.println(getConfiguration().toString());

      String appUrl = getConfiguration().getUrl();
      System.out.println("Test going to " + appUrl);
      getCurrentDriver().get(appUrl);

      MainPage mainPage = new MainPage(getCurrentDriver()).doWait();

      return mainPage;
    };
  }

}
