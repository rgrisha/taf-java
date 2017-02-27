package core.page.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PageElement {
  private By by;
  private int waitSeconds = Config.getWaitTime();
  private boolean doWait = true;
  private String elementName;
  private String findKeyStr;
  private IBy findBy;
  private String searchPattern;
  private WebElement cachedWebElement1;
  private boolean bLogging = false;
  private WebDriver webDriver;
  private int retryCount = 1;

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  public WebDriver getWebDriver() {
    return webDriver;
  }

  public PageElement setWebDriver(WebDriver driver) {
    webDriver = driver;
    return this;
  }

  PageElement(WebDriver driver) {
    setWebDriver(driver);
  }

  private WebElement getCachedWebElement() {
    return cachedWebElement1;
  }

  public PageElement setName(String name) {
    elementName = name;
    return this;
  }

  
  public String getName() {
    if (elementName == null || elementName.isEmpty()) {
      return "Element found by " + findBy + " \"" + searchPattern + "\"";
    }
    else {
      return elementName;
    }
  }

  interface IBy {
    By getBy(Object... args);

    String toString();
  }

  
  public PageElement toFindById(final String elementPattern) {
    searchPattern = elementPattern;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(elementPattern, args);
        return By.id(findKeyStr);
      }

      
      public String toString() {
        return "ID";
      }

    };
    return this;
  }

  
  public PageElement toFindByName(final String elementPattern) {
    searchPattern = elementPattern;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(elementPattern, args);
        return By.name(findKeyStr);
      }

      
      public String toString() {
        return "NAME";
      }
    };
    return this;
  }

  
  public PageElement toFindByXPath(final String elementPattern) {
    searchPattern = elementPattern;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(elementPattern, args);
        return By.xpath(findKeyStr);
      }

      
      public String toString() {
        return "XPATH";
      }
    };
    return this;
  }

  
  public PageElement toFindByPartialLinkText(final String elementPattern) {
    searchPattern = elementPattern;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(elementPattern, args);
        return By.partialLinkText(findKeyStr);
      }

      
      public String toString() {
        return "PART. LINK TEXT";
      }

    };
    return this;
  }

  
  public PageElement toFindByCssSelector(final String selText) {
    searchPattern = selText;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(selText, args);
        return By.cssSelector(findKeyStr);
      }

      
      public String toString() {
        return "CSS SEL. TEXT";
      }

    };
    return this;
  }

  
  public PageElement toFindByClassName(final String selText) {
    searchPattern = selText;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(selText, args);
        return By.className(findKeyStr);
      }

      
      public String toString() {
        return "CSS CLASS NAME";
      }

    };
    return this;
  }

  private boolean needToWait() {
    return doWait;
  }

  // this method will set both by and cachecWebElement properties
  
  public PageElement findElement(Object... args) {
    by = findBy.getBy(args);
    if (by == null) {
      throw new TestElementException("Cannot get by object for element searched by" + findKeyStr);
    }
    // sometimes it helps to retry
    if (needToWait()) {
      for (int i = 1; i <= getRetryCount(); i++) {
        try {
          waitFor(WaitCondition.presenceOfElementLocated());
          break;
        }
        catch (WebDriverException e) {
          if (i == getRetryCount()) { // if this is the last try, re-throw
            throw e;
          }
        }
      }
    }
    else {
      if (getCachedWebElement() == null) {
        setWebElement(getWebDriver().findElement(by));
      }
    }
    return this;
  }

  private int getWaitSeconds() {
    return this.waitSeconds;
  }

  
  public void setWebElement(WebElement element) {
    cachedWebElement1 = element;
  }

  private WebDriverWait getWaiter() {
    return new WebDriverWait(getWebDriver(), this.getWaitSeconds());
  }

  private Wait<WebDriver> getDriverWaiter() {

    Wait<WebDriver> wait = new FluentWait<>(getWebDriver())
        .withTimeout(getWaitSeconds(), TimeUnit.SECONDS)
        .pollingEvery(1, TimeUnit.SECONDS);
        //.ignoring(NoSuchElementException.class);
    return wait;
  }

  public void waitFor(Condition condition) {
    Object waitResult = getDriverWaiter().until(condition.getCondition(this));
    if (waitResult instanceof WebElement) {
      setWebElement((WebElement)waitResult);
    }
  }

  public List<PageElement> waitForPresenceOfMany() {
    List<WebElement> elements = getWaiter().until(ExpectedConditions.presenceOfAllElementsLocatedBy(getBy()));
    List<PageElement> pageElements = new ArrayList<>();
    for(WebElement element: elements) {
      PageElement pageElement = new PageElement(getWebDriver());
      pageElement.setWait(false);
      pageElement.setWebElement(element);
      pageElement.setName("element located by " + getBy().toString());
      pageElements.add(pageElement);
    }
    return pageElements;
  }


  
  public boolean waitForNegativeResult(Condition condition) {
    try {
      waitFor(condition);
    }
    catch (TimeoutException e) {
      return true;
    }
    return false;
  }

  private WebElement getWaitedElement(Condition condition, String caller) {
    if (needToWait()) {
      for (int i = 1; i <= getRetryCount(); i++) {
        try {
          waitFor(condition);
        }
        catch (WebDriverException e) {
          if (i == getRetryCount()) {
            throw e;
          }
        }
      }
    }
    return getCachedWebElement();
  }

  
  public void setValue(String sValue) {
    if (bLogging) {
      logger.info(String.format("Setting value %s to element %s", sValue, getName()));
    }
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "setValue").sendKeys(sValue);
  }

  
  public void setAttribute(String attributeName, String attributeValue) {
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "setAttribute");
    ((JavascriptExecutor)getWebDriver()).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                                                       getCachedWebElement(), attributeName, attributeValue);

  }

  
  public PageElement clear() {
    if (bLogging) {
      logger.info(String.format("Clearing value of element %s", getName()));
    }
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "clear").clear();
    return this;
  }

  public String getValue() {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "isValue").getText();
  }

  
  public boolean isSelected() {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "isValue").isSelected();
  }


  
  public String getAttribute(String attributeName) {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "getAttrbute").getAttribute(attributeName);
  }

  
  public PageElement click() {
    if (bLogging) {
      logger.info(String.format("Clicking element %s", getName()));
    }
    getWaitedElement(WaitCondition.elementToBeClickable(), "clickMe").click();
    return this;
  }

  
  public PageElement clickJs() {
    if (bLogging) {
      logger.info(String.format("JS-Clicking element %s", getName()));
    }
    getWaitedElement(WaitCondition.elementToBeClickable(), "clickMe").click();
    try{
      JavascriptExecutor executor = (JavascriptExecutor)getWebDriver();
      //executing javascript click inside try/catch to avoid new JS errors which blocks IE
      executor.executeScript("try{arguments[0].click();} catch(err) {}", getCachedWebElement());
    }
    catch (StaleElementReferenceException e){
      logger.error(String.format("Error: core.page reloaded and did not work for %s", getName()));
    }
    return this;
  }

  public PageElement moveToMe() {
    if (bLogging) {
      logger.info(String.format("Moving to element %s", getName()));
    }
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "moveToMe");

    try {
      Actions actions = new Actions(getWebDriver());
      actions.moveToElement(getCachedWebElement()).perform();
      return this;
    }
    catch (WebDriverException e) {
      logger.error(String.format("Error: element %s, moveToMe, exception: %s", getName(), e.getMessage()));
    }
    return this;
  }

  
  public PageElement moveToMeAndClick() {
    if (bLogging) {
      logger.info(String.format("Moving and clicking element %s", getName()));
    }
    getWaitedElement(WaitCondition.elementToBeClickable(), "moveToMeAndClick");

    try {
      Actions actions = new Actions(getWebDriver());
      actions.moveToElement(getCachedWebElement()).perform();
      actions.moveToElement(getCachedWebElement()).click().perform();
      return this;
    }
    catch (WebDriverException e) {
      logger.error(String.format("Error: element %s, moveToMeAndClick, exception: %s", getName(), e.getMessage()));
      throw new TestElementException(e.getMessage());
    }
  }

  
  public boolean isDisplayed() {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "isDisplayed").isDisplayed();
  }

  
  public boolean containsText(String text) throws TestElementException {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "containsText").getText().contains(text);
  }

  
  public boolean exists(Object... args) {
    setWebElement(null);
    try {
      //getWaitedElement(WaitCondition.presenceOfElementLocated(), "exists");
      findElement(args);
    }
    catch (WebDriverException e) {
      if (bLogging) {
        logger.info(String.format("Calling exists() on element %s, returns false", getName()));
      }
    }
    return getCachedWebElement() != null;
  }

  
  public boolean isEnabled() throws TestElementException {
    return getWaitedElement(WaitCondition.presenceOfElementLocated(), "isEnabled").isEnabled();
  }

  
  public void setDropDown(String sValue) throws TestElementException {

    getWaitedElement(WaitCondition.presenceOfElementLocated(), "setDropDown");
    Select selectBox = new Select(getCachedWebElement());

    try {
      selectBox.selectByVisibleText(sValue);
    }
    catch (NoSuchElementException a) {
      try {
        selectBox.selectByValue(sValue);
      }
      catch (NoSuchElementException b) {
        throw new TestElementException(String.format("Cannot select combo value '%s' on element '%s'",
                                                     sValue, this.elementName));
      }
    }
  }

  
  public By getBy() {
    if (by == null) {
      by = findBy.getBy();
    }
    return by;
  }

  
  public PageElement setWaitTime(int seconds) {
    waitSeconds = seconds;
    return this;
  }

  
  public PageElement setWait(boolean waitOrNot) {
    doWait = waitOrNot;
    return this;
  }

  
  public PageElement setLogging(boolean bDoLog) {
    bLogging = bDoLog;
    return this;
  }

  
  public WebElement getWebElement() {
    if (getCachedWebElement() == null) {
      getWaitedElement(WaitCondition.presenceOfElementLocated(), "getWebElement");
    }
    return getCachedWebElement();
  }

  /*
   * Requires By object - best is to feed a separate PageElement to it
  
  public List<PageElement> getChildren() {

  }
  */


  public List<PageElement> getList() {
    return waitForPresenceOfMany();
  }

  /**
  this is kinda workaround while CSS3 contains is not implemented in browsers
   */
  public Optional<PageElement> getFirstWithText(String text) {
    List<PageElement> elements = waitForPresenceOfMany();
    return elements.stream().filter(p -> p.getValue().contains(text)).findFirst();
  }

  public Logger getLogger() {
    return logger;
  }

  
  public SelectElement getAsSelect() throws TestElementException {
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "getAsSelect");
    Select selElement;
    selElement = new Select(getCachedWebElement());
    return new SelectElement(getWebDriver(), selElement);
  }

  
  public List<PageElement> getChildrenByTagName(String tagName) throws TestElementException {
    getWaitedElement(WaitCondition.presenceOfElementLocated(), "getChildrenByTagName");
    List<PageElement> elements = new ArrayList<PageElement>();
    if (getWebElement() == null) {
      return elements;
    }

    List<WebElement> webElements = getCachedWebElement().findElements(By.tagName(tagName));
    for (WebElement webElement : webElements) {
      PageElement element = new PageElement(getWebDriver());
      element.setWait(false);
      element.setWebElement(webElement);
      element.setName("child of " + getName() + "located by tagname " + tagName);
      elements.add(element);
    }
    return elements;
  }

  
  public String getFindKeyStr() {
    return findKeyStr;
  }

  
  public PageElement toFindSmart(final String pattern) {
    searchPattern = pattern;
    findBy = new IBy() {

      
      public By getBy(Object... args) {
        findKeyStr = String.format(pattern, args);
        return getBySmart(findKeyStr);
      }

      
      public String toString() {
        return "SMART NAME";
      }
    };
    return this;
  }

  private By getBySmart(String string) {

    By myBy;
    List<WebElement> elements;
    try {
      elements = getWebDriver().findElements(By.id(string));
      if (!elements.isEmpty()) {
        return By.id(string);
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by id " + e, e);
    }

    try {
      elements = getWebDriver().findElements(By.name(string));
      if (!elements.isEmpty()) {
        return By.name(string);
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by name " + e, e);
    }

    try {
      elements = getWebDriver().findElements(By.linkText(string));
      if (!elements.isEmpty()) {
        return By.linkText(string);
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by link text " + e, e);
    }

    try {
      elements = getWebDriver().findElements(By.xpath(string));
      if (!elements.isEmpty()) {
        return By.xpath(string);
      }
    }
    catch (InvalidSelectorException e) {
      logger.warn("Element "+ string +" wasn't found by xpath " + e, e);
    }

    try {
      myBy = By.xpath("//input[@value='" + string + "']");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by input value " + e, e);
    }

    try {
      myBy = By.xpath("//input[contains(@value, '" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by containing value " + e, e);
    }

    try {
      myBy = By.xpath("//a[@data-id='" + string + "']");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by data id " + e, e);
    }

    try {
      myBy = By.xpath("//a[@title='" + string + "']");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by title " + e, e);
    }

    try {
      myBy = By.xpath("//a[contains(@title,'" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by link title " + e, e);
    }

    try {
      myBy = By.xpath("//*[contains(@class,'" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by class " + e, e);
    }

    try {
      myBy = By.xpath("//a[contains(normalize-space(text()),'" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by link something " + e, e);
    }

    try {
      myBy = By.xpath("//a[contains(@href,'" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by link href " + e, e);
    }

    try {
      myBy = By.xpath("//*[contains(normalize-space(text()),'" + string + "')]");
      elements = getWebDriver().findElements(myBy);
      if (!elements.isEmpty()) {
        return myBy;
      }
    }
    catch (NoSuchElementException e) {
      logger.warn("Element "+ string +" wasn't found by something " + e, e);
    }

    //next line will throw NoSuchElementException
    throw new TestElementException("Unable to locate element by string: " + string);
  }

  
  public PageElement setRetries(int retryCount) {
    setRetryCount(retryCount);
    return this;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  
  public void clearCache() {
    setWebElement(null);
  }
}