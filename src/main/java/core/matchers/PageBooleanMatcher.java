package core.matchers;

import core.page.AbstractWebPage;
import org.testng.Assert;

/**
 * Custom matcher, core.page class vs boolean, allows chaining of test methods to be kept unbroken
 */
public class PageBooleanMatcher<T extends AbstractWebPage> {
  T page;
  boolean value;

  public PageBooleanMatcher(T page, boolean result) {
    this.page = page;
    this.value= result;
  }

  public T thenFail(String message) {
    Assert.assertFalse(isValue(), message);
    return page;
  }

  public T thenOkFailOtherwise(String message) {
    Assert.assertTrue(isValue(), message);
    return page;
  }

  public T failIfEqualTo(boolean bval, String message) {
    Assert.assertTrue(isValue() != bval, message);
    return page;
  }

  public T failIfNotEqualTo(boolean bval, String message) {
    Assert.assertTrue(isValue() == bval, message);
    return page;
  }

  public boolean isValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

}
