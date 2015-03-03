package edu.avans.hartigehap.web.it;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

@Slf4j
public class BrowserUtils {

  private static WebDriver webDriver = null;
  private static boolean shutdownHookEnabled = false;

  public static WebDriver getWebDriver() {
    if (webDriver == null) {
      if (!shutdownHookEnabled) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
          public void run() {
            close();
          }
        });
        shutdownHookEnabled = true;
      }
      /*
       * Vervang dit eventueel door een browser naar keuze.
       */
      webDriver = new FirefoxDriver();
    }

    /*
     * Stel een timeout in die aangeeft hoe lang de webDriver moet blijven proberen om een element dat op de pagina aanwezig zou moeten zijn te selecteren.
     */
    webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    return webDriver;
  }

  public static void close() {
    if (webDriver == null) {
      return;
    }
    try {
      webDriver.quit();
    }
    catch (Exception ex) {
      log.debug("", ex);
    }
    webDriver = null;
  }

}
