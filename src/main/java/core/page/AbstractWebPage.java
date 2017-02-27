package core.page;

import com.google.common.base.Predicate;
import core.config.EnvironmentConfiguration;
import core.matchers.PageStringMatcher;
import core.page.elements.PageElement;
import core.test.TestRunContext;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import core.page.elements.PageElementFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.apache.log4j.Logger;

public abstract class AbstractWebPage<P extends AbstractWebPage<P>> {

	final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
	protected WebDriverWait webDriverWait = null;
	int waitForPageToLoadInSeconds = 30;
	protected PageElementFactory elementFactory;

  EnvironmentConfiguration environmentConfiguration;



  public EnvironmentConfiguration getEnvironmentConfiguration() {
    return environmentConfiguration;
  }

  public void setEnvironmentConfiguration(EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  WebDriver driver;

	public WebDriver getWebDriver() {
		return driver;
	}

	public JavascriptExecutor getJavaScriptExecutor() {
		return (JavascriptExecutor) getWebDriver();
	}

	protected PageElement $(String cssLocator) {
		return elementFactory.toFindByCssSelector(cssLocator);
	}

	protected PageElement $n(String nameLocator) {
		return elementFactory.toFindByName(nameLocator);
	}

	protected PageElement $x(String xpathLocator) {
		return elementFactory.toFindByXPath(xpathLocator);
	}

	Map<String, Object> queryRow;

  public EnvironmentConfiguration getConfiguration() {
    return environmentConfiguration;
  }

	public interface SubTest<P> {
		P run(P page);
	}

	public P runSubTest(SubTest subTest) {
		return (P) subTest.run(this);
	}

	public interface SubTestRepeater<P, Q extends AbstractWebPage<Q>> {
		Q repeat(P page);
	}

	int repeatCount = 5;

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public <Q extends AbstractWebPage<Q>> Q repeatOnFail(SubTestRepeater<P, Q> repeater) {
		for (int i = repeatCount; i-- > 0;) {
      try {
        return repeater.repeat((P) this);
      } catch (WebDriverException e) {
        logger.info("Repeater retrying on page " + this.toString());
        continue;
      }
    }
		return repeater.repeat((P) this);
	}

	protected abstract void waitForPageToLoad();

	protected void afterLoad() {
	}

	public void waitForJsReadyState() {

		Predicate<WebDriver> isReadyStateComplete = driver1 -> "complete".equals(((JavascriptExecutor) driver1).executeScript("return document.readyState"));
		try {
			webDriverWait.until(isReadyStateComplete);
		} catch (WebDriverException e) {
		}
	}

	TestRunContext context;


	public AbstractWebPage(WebDriver driver) {
		this.driver = driver;
		webDriverWait = new WebDriverWait(getWebDriver(),waitForPageToLoadInSeconds);
		elementFactory = new PageElementFactory(driver);
	}

	public TestRunContext getTestRunContext() {
		if (this.context == null) {
			setTestRunContext(new TestRunContext());
		}
		return this.context;
	}

	public void setTestRunContext(TestRunContext context) {
		this.context = context;
	}

	public P saveValue(String name, Object value) {
		getTestRunContext().saveValue(name, value);
		return (P) this;
	}

	public Object getValue(String name) {
		return getTestRunContext().getSavedValue(name);
	}

	public P logSavedValues() {
		logger.info("******* dumping saved values: ***********");
		for (Map.Entry<String, Object> entry : getTestRunContext().getSavedValues().entrySet()) {
			logger.info(entry.getKey() + "=" + entry.getValue().toString());
		}
		logger.info("******* end of saved values *************");
		return (P) this;
	}

	public interface InvokerWithSavedValues<P> {
		P invoke(Map<String, Object> values, P page);
	}

	public P invokeWithSavedValues(InvokerWithSavedValues<P> invoker) {
		return invoker.invoke(getTestRunContext().getSavedValues(), (P) this);
	}

	public int getWaitForPageToLoadInSeconds() {
		return waitForPageToLoadInSeconds;
	}

	public void setWaitForPageToLoadInSeconds(int waitForPageToLoadInSeconds) {
		this.waitForPageToLoadInSeconds = waitForPageToLoadInSeconds;
	}

	public static <T extends AbstractWebPage<T>> T createPageInstance(Class<T> pageClass, WebDriver driver, TestRunContext context, EnvironmentConfiguration environmentConfiguration) {
		T newPageInstance;
		newPageInstance = PageFactory.initElements(driver, pageClass);
		newPageInstance.setTestRunContext(context);
		newPageInstance.waitForPageToLoad();
		newPageInstance.afterLoad();
    newPageInstance.setEnvironmentConfiguration(environmentConfiguration);
		return newPageInstance;
	}

	public <T extends AbstractWebPage<T>> T createPage(Class<T> pageClass) {
		return createPageInstance(pageClass, getWebDriver(),
				this.getTestRunContext(), getEnvironmentConfiguration());
	}

	public void acceptAlert() {
		for(int i = 10; i-->0;) {
			try {
				Thread.sleep(500);
				Alert alert = getWebDriver().switchTo().alert();
				alert.accept();
				break;
			} catch (InterruptedException e) {
			} catch (NoAlertPresentException e) {
				continue;
			}
		}

	}

	public boolean alertExists(String textPart) {
		try {
			boolean alertOk;
			Alert alert = getWebDriver().switchTo().alert();
			alertOk = alert.getText().contains(textPart);
			alert.accept();
			return alertOk;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	public void clickLinkLocatedByHrefPart(String hrefPart) {
		$("a[href *= \"%s\"]").findElement(hrefPart).click();
	}

	public void clickLinkLocatedByOnClickPart(String onClickPart) {
		elementFactory.toFindByCssSelector("a[onclick *= \"%s\"]").findElement(onClickPart).click();
	}

	public void clickInputLocatedByOnClickPart(String onClickPart) {
		elementFactory.toFindByCssSelector("input[onclick *= \"%s\"]").findElement(onClickPart).click();
	}

	public PageStringMatcher<P> getNamedTableTextField(String name) {
		return new PageStringMatcher<>((P) this, $x("//td[contains(.,'" + name + "')]/following-sibling::td[1]").getList().get(0).getValue());
	}

	protected void switchToIframe(final By by) {
		// for some reasons iframe is not droped
		getWebDriver().switchTo().defaultContent();
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(by).isDisplayed();
					logger.info(String.format("iframe element[%s] is visible", by));
					return true;
				} catch (NullPointerException e) {
					return false;
				} catch (NoSuchElementException e) {
					return false;
				}
			}
		};
		// could be that iframe is not settable because of active load!
		waitForJsReadyState();

		try {
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), 3000);
			wait.until(pageLoadCondition);
			WebElement el = getWebDriver().findElement(by);

			getWebDriver().switchTo().defaultContent();
			getWebDriver().switchTo().frame(el);
		} catch (Throwable e) {
			logger.error(String.format("iframe element[%s] not switchable", by));
			throw e;
		}
	}

	protected void switchBackToDefaultContent() {
		getWebDriver().switchTo().defaultContent();
	}
}