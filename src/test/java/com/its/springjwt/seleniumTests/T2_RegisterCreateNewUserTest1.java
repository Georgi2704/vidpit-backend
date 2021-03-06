package com.its.springjwt.seleniumTests;// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.jupiter.api.Order;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
public class T2_RegisterCreateNewUserTest1 {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", "E:\\Assignments\\S3 ITS Git Repository\\Backend\\springsecurityTest\\src\\main\\resources\\chromedriver.exe");
    driver = new ChromeDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  @Order(2)
  public void registerCreateNewUser() {
    driver.get("http://localhost:8081/");
    driver.manage().window().setSize(new Dimension(1062, 628));
    driver.findElement(By.id("loginButtonNavbar")).click();
    driver.findElement(By.cssSelector(".btn-info")).click();
    driver.findElement(By.id("exampleInputEmail1")).click();
    driver.findElement(By.id("exampleInputEmail1")).sendKeys("brice12@gmail.com");
    driver.findElement(By.id("exampleUsername1")).sendKeys("Brice2020");
    driver.findElement(By.id("exampleInputPassword1")).sendKeys("12341234");
    driver.findElement(By.id("exampleInputPassword2")).sendKeys("12341234");
    driver.findElement(By.cssSelector(".btn-lg")).click();
    driver.findElement(By.cssSelector(".mt-3")).click();
  }

  public static void ExplicitWait(WebDriver driver, String text){
    (new WebDriverWait(driver, 3)).until(ExpectedConditions
            .elementToBeClickable(By.linkText(text)));
  }

  public static void ExplicitWaitByID(WebDriver driver, String text){
    (new WebDriverWait(driver, 3)).until(ExpectedConditions
            .elementToBeClickable(By.id(text)));
  }
}
