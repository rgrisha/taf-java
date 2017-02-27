package core.page.elements;

import org.openqa.selenium.support.ui.ExpectedCondition;

public interface Condition {
  ExpectedCondition<?> getCondition(PageElement element);
}
