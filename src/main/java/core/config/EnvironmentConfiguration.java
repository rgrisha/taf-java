package core.config;

import core.driver.WebDriverFactory;
import org.apache.log4j.Logger;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class EnvironmentConfiguration {

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  Properties properties;
  private CountryFactory countryFactory;
  private EnvironmentFactory environmentFactory;

  public void setCountryFactory(CountryFactory countryFactory) {
    this.countryFactory = countryFactory;
  }

  public void setEnvironmentFactory(EnvironmentFactory environmentFactory) {
    this.environmentFactory = environmentFactory;
  }

  protected EnvironmentConfiguration(Properties properties) {
    this.properties = properties;
  }

  private boolean isMaskedConfigMethod(Method method) {
    for(Annotation annotation: method.getDeclaredAnnotations()) {
      if (annotation instanceof ConfigProperty) {
        if (((ConfigProperty)annotation).mask()) {
          return true;
        }
      }
    }
    return false;
  }

  private void toStringParameterisedMethod(Method method, StringBuilder stringsOut) throws InvocationTargetException, IllegalAccessException {
    if(method.getParameterCount() == 1) {
      if(method.getParameterTypes()[0] == Country.class) {
        stringsOut.append(method.getName());
        stringsOut.append("(");
        stringsOut.append(getCountry());
        stringsOut.append(")=");
        stringsOut.append(method.invoke(this, getCountry()));
      }
      else if(method.getParameterTypes()[0] == Environment.class) {
        stringsOut.append(method.getName());
        stringsOut.append("(");
        stringsOut.append(getEnvironment());
        stringsOut.append(")=");
        stringsOut.append(method.invoke(this, getEnvironment()));
      }
    }
  }

  public String toString() {
    StringBuilder strings = new StringBuilder();

    for (Method method : getClass().getMethods()) {
      if (method.isAnnotationPresent(ConfigProperty.class)) {
        if(isMaskedConfigMethod(method)) {
          strings.append(method.getName());
          strings.append("=*******");
        }
        else {
          try {
            if(method.getParameterCount() > 0) {
              toStringParameterisedMethod(method, strings);
            }
            else {
              strings.append(method.getName());
              strings.append("=");
              strings.append(method.invoke(this));
            }
          }
          catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Error when converting configuration to string, method " + method.getName() + " " + e, e);
          }
        }
      }
      strings.append(System.lineSeparator());
    }
    return strings.toString();
  }

  protected String get(String key, Country country) {
    return get(key + "." + country);
  }

  protected String get(String key, Environment environment ) {
    return get(key + "." + environment);
  }

  protected String get(String key) {
    if(System.getProperty("taf.config." + key) != null) {
      return System.getProperty("taf.config." + key);
    }
    if(System.getenv("taf.env." + key) != null) {
      return System.getenv("taf.env." + key);
    }
    return properties.getProperty(key);
  }

  protected String get(String key, String defaultValue) {
    String ret = get(key);
    if (ret == null) {
      return defaultValue;
    }
    return ret;
  }

  @ConfigProperty
  public Country getCountry() {
    return countryFactory.create(get("country").toUpperCase());
  }

  @ConfigProperty
  public String getUrl() {
    return get("url");
  }

  @ConfigProperty
  public String getUsername() {
    return get("username");
  }

  @ConfigProperty(mask=true)
  public String getPassword() {
    return get("password");
  }

  @ConfigProperty
  public String getDbUrl() {
    return get("dbUrl");
  }

  @ConfigProperty
  public String getDbUsername() {
    return get("dbUsername");
  }

  @ConfigProperty(mask=true)
  public String getDbPassword() {
    return get("dbPassword");
  }

  @ConfigProperty
  public String getBrowser() {
    return get("browser", WebDriverFactory.DEFAULT_BROWSER);
  }

  @ConfigProperty
  public String getPlatform() {
    return get("platform", "windows");
  }

  @ConfigProperty
  public String getProxy() {
    return get("proxy");
  }

  @ConfigProperty
  public String getGridUrl() {
    return get("gridUrl");
  }

  @ConfigProperty
  public boolean getLogPageActions() {
    return Boolean.parseBoolean(get("logPageActions"));
  }

  @ConfigProperty
  public long getDriverTimeout() {
    try {
      return Long.valueOf(get("driverTimeout"));
    }
    catch (NumberFormatException ex) {
      return 30;
    }
  }

  @ConfigProperty
  public Environment getEnvironment(){
    return environmentFactory.create(get("env"));
  }

  @ConfigProperty
  public String getDriverPath() {
    return get("driverPath");
  }

  @ConfigProperty
  public String getTestDataFile() {
    return get("testdata.file");
  }
}
