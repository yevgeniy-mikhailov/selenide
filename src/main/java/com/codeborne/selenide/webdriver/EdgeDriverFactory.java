package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

@ParametersAreNonnullByDefault
public class EdgeDriverFactory extends AbstractChromiumDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.edge.driver")) {
      WebDriverManager.edgedriver().setup();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    EdgeOptions options = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    EdgeDriverService driverService = createDriverService(config);
    return new EdgeDriver(driverService, options);
  }

  private EdgeDriverService createDriverService(Config config) {
    return withLog(config, new EdgeDriverService.Builder());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public EdgeOptions createCapabilities(Config config, Browser browser,
                                        @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    MutableCapabilities capabilities = createCommonCapabilities(new EdgeOptions(), config, browser, proxy);
    capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);

    EdgeOptions options = new EdgeOptions().merge(capabilities);
    options.setHeadless(config.headless());

    if (config.browserBinary() == null) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in Edge, setting will be ignored.");
    }

    options.addArguments(createEdgeArguments(config));
    options.setExperimentalOption("prefs", prefs(browserDownloadsFolder, System.getProperty("edgeoptions.prefs", "")));
    return options;
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createEdgeArguments(Config config) {
    return createChromiumArguments(config, System.getProperty("edgeoptions.args"));
  }
}
