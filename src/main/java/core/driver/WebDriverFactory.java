package core.driver;

import core.config.EnvironmentConfiguration;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.apache.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

public class WebDriverFactory {

  private static final String PROPERTY = "property";

  private static final String PROXY = "proxy";

  private static final String TIMEOUT = "timeout";

  private static final String PLATFORM = "platform";
  private static final String PLATFORM_WINDOWS = "windows";
  private static final String PLATFORM_UNIX = "unix";

  private static final String BROWSER = "browser";
  private static final String BROWSER_FIREFOX = "firefox";
  private static final String BROWSER_CHROME = "chrome";
  private static final String BROWSER_IE = "ie";

  public static final String DEFAULT_BROWSER = BROWSER_FIREFOX;

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  public static WebDriver getWebDriver(EnvironmentConfiguration conf) {
    WebDriver remoteWebDriver = null;
    try {
      DesiredCapabilities capabilities = getCapabilities(conf);
      remoteWebDriver = getDriver(conf, capabilities);
      setTimeout(conf, remoteWebDriver);
    }
    catch (Exception e) {
      logger.error("Exception when initializing the driver: " + e, e);
      System.out.println("Exception when initializing driver: " + e);
      if (remoteWebDriver != null) {
        remoteWebDriver.quit();
      }
    }
    return remoteWebDriver;
  }

  private static void setTimeout(EnvironmentConfiguration conf, WebDriver remoteWebDriver) {
    long timeoutToSet = conf.getDriverTimeout();
    remoteWebDriver.manage().timeouts().implicitlyWait(timeoutToSet, TimeUnit.SECONDS);
  }

  private static void setDriverPathIfNeeded(String propertyName, EnvironmentConfiguration conf) {
    if( System.getProperty(propertyName) == null && conf.getDriverPath() != null ) {
      System.setProperty(propertyName, conf.getDriverPath());
    }
  }

  private static WebDriver getDriver(EnvironmentConfiguration conf, DesiredCapabilities capabilities) {
    String browser = conf.getBrowser();
    if (browser.equalsIgnoreCase(BROWSER_FIREFOX)) {
      return new FirefoxDriver(capabilities);
    }
    else if (browser.equalsIgnoreCase(BROWSER_CHROME) && conf.getGridUrl() == null) {
      return new ChromeDriver(capabilities);
    }
    else if (browser.equalsIgnoreCase(BROWSER_IE)) {
      return new InternetExplorerDriver(capabilities);
    }
    throw new IllegalArgumentException("Illegal browser specified");
  }

  private static DesiredCapabilities getCapabilities(EnvironmentConfiguration conf) {

    DesiredCapabilities cap = new DesiredCapabilities();
    Proxy proxy = getProxyIfNeeded(conf);

    if (proxy != null){
      cap.setCapability(CapabilityType.PROXY, proxy);
    }

    String platform = conf.getPlatform();
    try {
      Platform plat = Platform.valueOf(platform.toUpperCase());
      cap.setPlatform(plat);
    } catch (IllegalArgumentException e){
      cap.setPlatform(Platform.LINUX);
    }
    return cap;
  }

  private static Proxy getProxyIfNeeded(EnvironmentConfiguration conf) {
    String proxyProp = conf.getProxy();
    if (proxyProp == null) {
      return null;
    }
    Proxy proxy = new Proxy();
    proxy.setHttpProxy(proxyProp).setFtpProxy(proxyProp);
    return proxy;

  }

}