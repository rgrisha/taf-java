package config;

import core.config.Environment;

import java.lang.invoke.MethodHandles;

/**
 * Environments "enum" implemented using extensible enum pattern.
 * Not done using plain enum because we want to extend Environment
 * by class that exists in test package
 */
public class EnvironmentImpl implements Environment {

  private final String name;

  private EnvironmentImpl(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  public static final EnvironmentImpl DEV     = new EnvironmentImpl("DEV");
  public static final EnvironmentImpl PROD    = new EnvironmentImpl("PROD");

  private static final EnvironmentImpl[] POSSIBLE_VALUES = {DEV, PROD};

  @Override
  public Environment fromString(String env) {
    return createEnvironment(env);
  }

  @Override
  public boolean equals(Environment anotherEnvironment) {
    return toString().equals(anotherEnvironment.toString());
  }

  public static Environment createEnvironment(String env) {
    for(EnvironmentImpl environmentImpl: POSSIBLE_VALUES) {
      if(environmentImpl.toString().equals(env.toString())) {
        return environmentImpl;
      }
    }
    throw new RuntimeException("Value " + env + " cannot be casted to " + MethodHandles.lookup().lookupClass().getCanonicalName());
  }

}
