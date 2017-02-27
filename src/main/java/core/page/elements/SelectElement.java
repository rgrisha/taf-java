package core.page.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.apache.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class SelectElement extends PageElement {

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  Select selectWebElement;

  public SelectElement(WebDriver driver, Select selectWebElement) {
    super(driver);
    this.selectWebElement = selectWebElement;
  }

  public void deselectAll() {
    selectWebElement.deselectAll();
  }

  public void deselectByIndex(int index) {
    selectWebElement.deselectByIndex(index);
  }

  public void deselectByValue(String value) {
    selectWebElement.deselectByValue(value);
  }

  public void deselectByVisibleText(String text) {
    selectWebElement.deselectByVisibleText(text);
  }

  private List<PageElement> fromWebElementsToPageElements(List<WebElement> webElements) {
    List<PageElement> elements = new ArrayList<PageElement>();
    for (WebElement webElement : webElements) {
      PageElement element = new PageElement(getWebDriver());
      element.setWebElement(webElement);
      element.setWait(false);
      elements.add(element);
    }
    return elements;
  }

  public List<PageElement> getAllSelectedOptions() {
    List<WebElement> webElements = selectWebElement.getAllSelectedOptions();
    return fromWebElementsToPageElements(webElements);
  }

  public PageElement getFirstSelectedOption() {
    WebElement firstSelected = selectWebElement.getFirstSelectedOption();
    PageElement element = new PageElement(getWebDriver());
    element.setWebElement(firstSelected);
    element.setWait(false);
    return element;
  }

  public List<PageElement> getOptions() {
    List<WebElement> webElements = selectWebElement.getOptions();
    return fromWebElementsToPageElements(webElements);
  }

  public boolean isMultiple() {
    return selectWebElement.isMultiple();
  }

  public void selectByIndex(int index) {
    selectWebElement.selectByIndex(index);
  }

  public void selectByValue(String value) {
    selectWebElement.selectByValue(value);
  }

  public void selectByVisibleText(String text) {
    selectWebElement.selectByVisibleText(text);
  }

  public boolean selectByPartialText(String partialText) throws TestElementException {
    for (PageElement element : getOptions()) {
      if (element.containsText(partialText)) {
        selectByVisibleText(element.getValue());
        return true;
      }
    }
    return false;
  }
}
