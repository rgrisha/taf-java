package loancalc.config;

import core.config.ConfigProperty;
import core.config.EnvironmentConfiguration;

import java.util.Properties;

public class LoanCalculatorConfiguration extends EnvironmentConfiguration {

  public static final String COUNTRY_CODE_KEY = "country";

  public LoanCalculatorConfiguration(Properties properties) {
    super(properties);
  }


  @ConfigProperty
  public String getEligibleAmount() {
    return get("eligibleAmount");
  }

  @ConfigProperty
  public String getSalaryAmount() {
    return get("salaryAmount");
  }

  @ConfigProperty
  public String getExpectedLoanResult() {
    return get("expectedLoanResult");
  }

}
