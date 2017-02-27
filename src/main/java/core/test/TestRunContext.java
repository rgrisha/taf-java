package core.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Passed to each page to store run context
 * Can save and restore values from various pages
 */

public class TestRunContext {
  Map<String, Object> savedValues = new HashMap<>();

  public Map<String, Object> getSavedValues() {
    return savedValues;
  }

  public void saveValue(String name, Object value) {
    savedValues.put(name, value);
  }

  public Object getSavedValue(String name) {
    return savedValues.get(name);
  }

}
