package core.config;

public interface Environment {
  boolean equals(Environment environment);
  Environment fromString(String environment);

}
