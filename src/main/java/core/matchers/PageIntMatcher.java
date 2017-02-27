package core.matchers;

import core.page.AbstractWebPage;
import org.testng.Assert;

/**
 * Custom matcher, core.page class vs int, allows chaining of test methods to be kept unbroken
 */
public class PageIntMatcher<T extends AbstractWebPage> {
  T page;
  int value;

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public PageIntMatcher(T page, int value) {
    this.page = page;
    this.value = value;
  }

  public T failIfLessThan(int another, String message) {
    Assert.assertTrue(getValue() >= another, "(" + getValue() + ") " + message);
    return page;
  }

  public T failIfMoreThan(int another, String message) {
    Assert.assertTrue(getValue() <= another, "(" + getValue() + ") " + message);
    return page;
  }

  public T failIfNotBetween(int low, int high, String message) {
    Assert.assertTrue(getValue() >= low && getValue() <= high, "(" + getValue() + ") " + message);
    return page;
  }
}
