package core.test;

import com.google.common.base.Function;
import core.config.*;
import core.driver.WebDriverFactory;
import core.page.AbstractWebPage;
import core.page.elements.PageElementFactory;
import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstract class for test cases.
 * Test class -> Base app test class -> AbstractTestCase
 */

public abstract class AbstractTestCase<C extends EnvironmentConfiguration> {

  public static final String CONFIG = "config";
  public static final String PROPERTIES = "properties";
  public static final String TEST_DATA = "testData";
  public static final String DATA_SUFFIX = "xml";

  private CountryFactory countryFactory;
  private EnvironmentFactory environmentFactory;

  private AbstractTestCase() {}

  public AbstractTestCase(CountryFactory countryFactory, EnvironmentFactory environmentFactory) {
    this.countryFactory = countryFactory;
    this.environmentFactory = environmentFactory;
  }

  static ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();

  public static WebDriver getCurrentDriver() {
    return CURRENT_DRIVER.get();
  }

  public static void setCurrentDriver(WebDriver currentDriver) {
    AbstractTestCase.CURRENT_DRIVER.set(currentDriver);
  }

  C configuration;

  String configurationFileName;

  public String getConfigurationFileName() {
    return configurationFileName;
  }

  public void setConfigurationFileName(String configurationFileName) {
    this.configurationFileName = configurationFileName;
  }

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  public TestRunContext getTestRunContext() {
    return testRunContext;
  }

  TestRunContext testRunContext = new TestRunContext();

  public interface StartPage<P extends AbstractWebPage<P>> {
    P getStartPage(TestRunContext context);
  }

  protected <P extends AbstractWebPage<P>> P getMainPage(StartPage<P> startPage) {
    P newStartPage = startPage.getStartPage(getTestRunContext());
    newStartPage.setTestRunContext(testRunContext);
    return newStartPage;
  }

  public C getConfiguration() {
    if(configuration == null) {

      ConfigurationFactory<C> configurationFactory = new ConfigurationFactory<>(getConfigurationFileName());
      C conf = configurationFactory.initializeConfiguration(this.getClass(), getConfigurationClass());

      conf.setCountryFactory(this.countryFactory);
      conf.setEnvironmentFactory(this.environmentFactory);
      configuration = conf;
    }
    return configuration;
  }

  public Path getFileRelative(Class<?> sClass, String rootDirectory, String fileSuffix) {

    String country = getConfiguration().getCountry().toString().toLowerCase();
    Path configWithCountry = Paths.get(System.getProperty("user.dir")).resolve(rootDirectory).resolve(country).resolve(sClass.getSimpleName() + "." + fileSuffix);
    if (configWithCountry.toFile().exists()) {
      return configWithCountry;
    }

    Path testClassPath = Paths.get(System.getProperty("user.dir")).resolve(rootDirectory).resolve(sClass.getSimpleName() + "." + fileSuffix);
    if (testClassPath.toFile().exists()) {
      return testClassPath;
    }

    return null;
  }

  public Path getTestDataFilePath(Class<?> sClass, String rootDirectory, String fileSuffix) {
    String testDataFile = getConfiguration().getTestDataFile();
    if(testDataFile != null) {
      return Paths.get(testDataFile);
    }
    return getFileRelative(sClass, rootDirectory, fileSuffix);
  }

  public Class<C> getConfigurationClass() {
    Class<?> upperClass;
    for(upperClass = this.getClass(); !AbstractTestCase.class.equals(upperClass.getSuperclass()); upperClass = upperClass.getSuperclass()){}
    Type superType = upperClass.getGenericSuperclass();
    Type typeOfC = ((ParameterizedType) superType).getActualTypeArguments()[0];

    if(typeOfC instanceof Class) {
      //I am bleeding here
      return (Class<C>)typeOfC;
    }
    else {
      return null;
    }
  }

  @BeforeClass
  public void initClass() {
    //DOMConfigurator.configure(Paths.get("config","log4j.xml").toString());
    // Not proud of this tight coupling
    PageElementFactory.setLogPageActions(getConfiguration().getLogPageActions());
  }

  @BeforeMethod
  public void initTestMethod() {
    if (getCurrentDriver() == null) {
      WebDriver driver = WebDriverFactory.getWebDriver(configuration);
      driver.manage().window().maximize();
      setCurrentDriver(driver);
    }
  }

  @AfterMethod
  public void deinitTestMethod() {
    if (getCurrentDriver() != null) {
      getCurrentDriver().quit();
      setCurrentDriver(null);
    }
  }

}
