package config;

import core.config.Environment;
import core.config.EnvironmentFactory;

/**
 * Created by rolandas on 2/27/17.
 */
public class EnvironmentFactoryImpl implements EnvironmentFactory {
  @Override
  public Environment create(String environment) {
    return EnvironmentImpl.createEnvironment(environment);
  }
}
