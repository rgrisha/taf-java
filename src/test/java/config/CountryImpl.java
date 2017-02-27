package config;


import core.config.Country;

import java.lang.invoke.MethodHandles;

/**
 * Countries "enum" implemented using extensible enum pattern.
 * Not done using plain enum because we want to extend Country
 * by class that exists in test package
 */
public class CountryImpl implements Country {

  private final String name;

  private CountryImpl(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  public static final CountryImpl LT    = new CountryImpl("LT");
  public static final CountryImpl LV    = new CountryImpl("LV");
  public static final CountryImpl EE    = new CountryImpl("EE");

  private static final CountryImpl[] POSSIBLE_VALUES = { LT, LV, EE };

  @Override
  public Country fromString(String co) {
    return createCountry(co);
  }

  @Override
  public boolean equals(Country anotherCountry) {
    return toString().equals(anotherCountry.toString());
  }

  public static Country createCountry(String co) {
    for(CountryImpl countryImpl: POSSIBLE_VALUES) {
      if(countryImpl.toString().equals(co.toString())) {
        return countryImpl;
      }
    }
    throw new RuntimeException("Value " + co + " cannot be casted to " + MethodHandles.lookup().lookupClass().getCanonicalName());
  }
}
