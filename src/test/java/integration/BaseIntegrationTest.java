package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.junit5.TextReportExtension;
import integration.server.LocalHttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.SAFARI;
import static java.lang.Boolean.parseBoolean;
import static org.openqa.selenium.net.PortProber.findFreePort;

@ExtendWith({LogTestNameExtension.class, TextReportExtension.class})
public abstract class BaseIntegrationTest {
  private final Logger log = LoggerFactory.getLogger(getClass());

  protected static volatile LocalHttpServer server;
  private static String protocol;
  private static int port;
  protected static final String browser = System.getProperty("selenide.browser", CHROME);
  private static final boolean SSL = !SAFARI.equalsIgnoreCase(browser) && !FIREFOX.equalsIgnoreCase(browser);
  static final boolean headless = parseBoolean(System.getProperty("selenide.headless", "false"));

  @BeforeAll
  static void setUpAll() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  final void resetUploadedFiles() throws Exception {
    runLocalHttpServer();
    server.reset();
  }

  private void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (BaseIntegrationTest.class) {
        if (server == null) {
          port = findFreePort();
          log.info("Starting local http server on port {}, ssl: {}", port, SSL);
          server = new LocalHttpServer(port, SSL).start();
          protocol = SSL ? "https://" : "http://";
        }
      }
    }
  }

  protected static String getBaseUrl() {
    return protocol + "127.0.0.1:" + port;
  }

  protected static Browser browser() {
    return new Browser(browser, headless);
  }
}
