package config;

import core.config.Country;
import core.config.CountryFactory;

/**
 * Created by rolandas on 2/27/17.
 */
public class CountryFactoryImpl implements CountryFactory {
  @Override
  public Country create(String country) {
    return CountryImpl.createCountry(country);
  }
}
