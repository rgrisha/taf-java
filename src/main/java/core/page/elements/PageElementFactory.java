package core.page.elements;

import org.openqa.selenium.WebDriver;
import org.apache.log4j.Logger;

import java.lang.invoke.MethodHandles;

public class PageElementFactory {

  WebDriver webDriver;
  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
  String cssPrefix = "";

  static Boolean LOG_PAGE_ACTIONS = null;

  public static void setLogPageActions(Boolean logPageActions) {
    LOG_PAGE_ACTIONS = logPageActions;
  }

  public static boolean getLogPageActions() {
    return LOG_PAGE_ACTIONS != null & LOG_PAGE_ACTIONS;
  }

  public PageElementFactory(WebDriver driver) {
    setWebDriver(driver);
  }

  public PageElementFactory(WebDriver driver, String cssPrefix) {
    setWebDriver(driver);
    this.cssPrefix = cssPrefix;
  }

  public void setWebDriver(WebDriver driver) {
    this.webDriver = driver;
  }

  public WebDriver getWebDriver() {
    return webDriver;
  }

  public PageElement toFindById(String pattern) {
    return new PageElement(getWebDriver()).toFindById(pattern).setLogging(getLogPageActions());
  }

  public PageElement toFindByName(String pattern) {
    return new PageElement(getWebDriver()).toFindByName(pattern).setLogging(getLogPageActions());
  }

  public PageElement toFindByXPath(String pattern) {
    return new PageElement(getWebDriver()).toFindByXPath(pattern).setLogging(getLogPageActions());
  }

  public PageElement toFindByPartialLinkText(String pattern) {
    return new PageElement(getWebDriver()).toFindByPartialLinkText(pattern).setLogging(getLogPageActions());
  }

  public PageElement toFindByCssSelector(String pattern) {
    return new PageElement(getWebDriver()).toFindByCssSelector(cssPrefix + pattern).setLogging(getLogPageActions());
  }

  public PageElement toFindByClassName(String pattern) {
    return new PageElement(getWebDriver()).toFindByClassName(pattern).setLogging(getLogPageActions());
  }

}

