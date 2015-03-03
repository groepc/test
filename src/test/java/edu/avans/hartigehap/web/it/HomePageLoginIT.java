package edu.avans.hartigehap.web.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


@Slf4j
public class HomePageLoginIT {

  public static String URL = "http://localhost:8080/hh";

  @Test
  public void login() {
    WebDriver driver = BrowserUtils.getWebDriver();
    driver.get(URL);
    log.debug("Congratulations, the home page is available ;-) {}", URL);
    WebElement loginDiv = driver.findElement(By.id("login"));
    assertNotNull(loginDiv);
    WebElement nameInput = loginDiv.findElement(By.name("j_username"));
    assertNotNull(nameInput);
    nameInput.sendKeys("erco");
    WebElement passwordInput = loginDiv.findElement(By.name("j_password"));
    passwordInput.sendKeys("erco");
    assertNotNull(passwordInput);

    driver.findElement(By.name("submit")).click();
    try {
      WebElement errorDiv = driver.findElement(By.className("error"));
      fail("For a succesful login, an error div is not expected: " + errorDiv);
    }
    catch (NoSuchElementException ex) {
      log.debug("Login succeeded ;-)");
    }
  }

}
