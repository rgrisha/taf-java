package core.page.elements;

public class TestElementException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public TestElementException(String message) {
    super("Error in core.page.elements: " + message);
  }

  public TestElementException(String message, Object... args) {
    super("Error in core.page.elements: " + String.format(message, args));
  }

}