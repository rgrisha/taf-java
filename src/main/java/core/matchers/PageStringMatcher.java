package core.matchers;

import core.page.AbstractWebPage;
import org.testng.Assert;

/**
 * Custom matcher, core.page class vs String, allows chaining of test methods to be kept unbroken
 */
public class PageStringMatcher<T extends AbstractWebPage> {
  T page;
  String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public PageStringMatcher(T page, String value) {
    this.page = page;
    this.value = value;
  }

  public T failIfEqualsTo(String another, String message) {
    Assert.assertFalse(getValue().equals(another), message);
    return page;
  }

  public T failIfNotEqualsTo(String another, String message) {
    Assert.assertTrue(getValue().equals(another), message);
    return page;
  }

  public T saveValueAs(String name) {
    page.saveValue(name, getValue());
    return page;
  }
}
