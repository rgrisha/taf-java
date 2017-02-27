package core.config;

public interface Country {
  boolean equals(Country country);
  Country fromString(String country);
}
