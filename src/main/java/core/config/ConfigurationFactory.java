package core.config;

import core.test.AbstractTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ConfigurationFactory<C extends EnvironmentConfiguration> {

  public static final String CONFIG = "config";
  public static final String PROPERTIES = "properties";

  final static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

  String propertyFileName = null;

  public ConfigurationFactory(String propertyFileName) {
    this.propertyFileName = propertyFileName;
  }

  public static Properties getPropertiesFromFile(Path pathToFile) {
    try {
      Properties properties = new Properties();
      InputStream inputStream = Files.newInputStream(pathToFile);
      properties.load(inputStream);
      inputStream.close();
      return properties;
    } catch (IOException e) {
      logger.fatal("Failed to load config file " + pathToFile + ". " + e, e);
      throw new RuntimeException(e);
    }
  }

  public C getConfigurationInstance(Class<C> clazz, Properties properties) {
    try {
      Constructor<C> constructor = clazz.getConstructor(Properties.class);
      return constructor.newInstance(properties);
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      logger.error("Error creating configuration instance " + clazz.getSimpleName() + " " + e, e);
      return null;
    }
  }

  public <T extends AbstractTestCase> C initializeConfiguration(Class<T> clazz, Class<C> configClazz) {
    Path configPath = getConfigFilePath(CONFIG, PROPERTIES, clazz);

    if (configPath == null) {
      logger.fatal("Cannot find configuration file");
    }
    Properties properties = getPropertiesFromFile(configPath);

    return getConfigurationInstance(configClazz, properties);
  }

  public <T extends AbstractTestCase<C>> String getPropertyFileName(Class<T> clazz) {
    if (propertyFileName == null || propertyFileName.isEmpty()) {
      return clazz.getSimpleName();
    }
    return propertyFileName;
  }


  public <T extends AbstractTestCase<C>> Path getConfigFilePath(String rootDirectory, String fileSuffix, Class<T> testCaseClass) {

    if (System.getProperty("taf.config.file") != null && !"".equals(System.getProperty("taf.config.file"))) {
      return Paths.get(System.getProperty("taf.config.file"));
    }

    Path configFilePath = Paths.get(System.getProperty("user.dir")).resolve(rootDirectory).resolve(getPropertyFileName(testCaseClass) + "." + fileSuffix);
    if (configFilePath.toFile().exists()) {
      return configFilePath;
    } else {
      throw new RuntimeException("Configuration file was not found neither by config property 'taf.config.file' or property file name " + configFilePath);
    }

  }

}
