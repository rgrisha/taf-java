package core.page.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WaitCondition {

  public static Condition elementSelected() {
    return new Condition() {

      @Override
      public ExpectedCondition<Boolean> getCondition(PageElement element) {
        return ExpectedConditions.elementSelectionStateToBe(element.getBy(), true);
      }

    };
  }

  public static Condition elementUnSelected() {
    return new Condition() {

      @Override
      public ExpectedCondition<Boolean> getCondition(PageElement element) {
        return ExpectedConditions.elementSelectionStateToBe(element.getBy(), false);
      }

    };
  }

  public static Condition elementToBeClickable() {
    return new Condition() {

      @Override
      public ExpectedCondition<WebElement> getCondition(PageElement element) {
        return ExpectedConditions.elementToBeClickable(element.getBy());
      }

    };
  }


  public static Condition presenceOfElementLocated() {
    return new Condition() {

      @Override
      public ExpectedCondition<WebElement> getCondition(PageElement element) {
        return ExpectedConditions.presenceOfElementLocated(element.getBy());
      }

    };

  }

  public static Condition elementToBeRemovedFromDOM() {
    return new Condition() {

      @Override
      public ExpectedCondition<Boolean> getCondition(PageElement element) {
        return ExpectedConditions.stalenessOf(element.getWebElement());
      }

    };
  }

  public static Condition elementToHaveText(final String textToWaitFor) {
    return new Condition() {

      @Override
      public ExpectedCondition<Boolean> getCondition(PageElement element) {
        return ExpectedConditions.textToBePresentInElementValue(element.getBy(), textToWaitFor);
      }

    };
  }


  public static Condition elementValueToHaveText(final String textToWaitFor) {
    return new Condition() {

      @Override
      public ExpectedCondition<Boolean> getCondition(PageElement element) {
        return ExpectedConditions.textToBePresentInElementValue(element.getBy(), textToWaitFor);
      }

    };
  }

  public static Condition elementToBeVisible() {
    return new Condition() {

      @Override
      public ExpectedCondition<WebElement> getCondition(PageElement element) {
        return ExpectedConditions.visibilityOf(element.getWebElement());
      }

    };
  }

  public static Condition frameToBeAvailable() {
    return new Condition() {

      @Override
      public ExpectedCondition<WebDriver> getCondition(PageElement element) {
        By by = element.getBy();
        if (by instanceof By.ByName || by instanceof By.ById) {
          return ExpectedConditions.frameToBeAvailableAndSwitchToIt(element.getFindKeyStr());
        }
        else {
          return null;
        }
      }

    };
  }

}
