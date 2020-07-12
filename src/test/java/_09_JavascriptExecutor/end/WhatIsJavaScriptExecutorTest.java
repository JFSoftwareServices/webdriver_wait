package _09_JavascriptExecutor.end;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/*
    - driver can be cast to ((JavascriptExecutor) driver)
    - allowing arbitrary JavaScript to be executed on the page
    - synchronously exec.executeScript
    - and asynchronously exec.executeAsyncScript
 */
public class WhatIsJavaScriptExecutorTest {

    WebDriver driver;

    @BeforeAll
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void canManipulatePageWithJavaScript_sync() {

        driver = new ChromeDriver();

        driver.get("https://eviltester.github.io/synchole/collapseable.html");

        final WebElement heading = driver.findElement(
                By.cssSelector("section.synchole > h2"));

        // cast the driver to JavascriptExecutor
        // use exec to change the inner text of the heading
        // assert on the changed Text
        // arguments array is passed in by webDriver
        JavascriptExecutor exec = (JavascriptExecutor) driver;
        exec.executeScript("arguments[0].innerText=arguments[1]",
                heading, "My New Heading Text");

        Assertions.assertEquals("My New Heading Text",
                heading.getText());

    }

    @Test
    public void canManipulatePageWithJavaScript_async_basic() {

        driver = new ChromeDriver();

        driver.get("https://eviltester.github.io/synchole/collapseable.html");

        final WebElement heading = driver.findElement(
                By.cssSelector("section.synchole > h2"));

        // webDriver runs the code and waits for a callback function to be executed. Callback function is automatically
        // added to the arguments array by webDriver as the last argument. We do not write a callback function ourselves
        // but we must call the callback function otherwise test will fail since it will just wait for the callback
        // function to run and eventually there will be a scriptTimeoutException

        // We are artificially calling the call back function, normally we would pass the call back function into
        // something on the page for it to do some work and then execute and call us.

        //last test better demonstrates how to do this

        JavascriptExecutor exec = (JavascriptExecutor) driver;
        exec.executeAsyncScript("arguments[0].innerText=arguments[1];" + "arguments[arguments.length -1]();", // we call the callback function here
                heading, "My New Heading Text");

        Assertions.assertEquals("My New Heading Text",
                heading.getText());
    }

    @Test
    public void canManipulatePageWithJavaScript_async_better_demo() {

        driver = new ChromeDriver();

        driver.get("https://eviltester.github.io/synchole/collapseable.html");

        final WebElement heading = driver.findElement(
                By.cssSelector("section.synchole > h2"));

        // use setTimeout to call code i  the setTimeout after a period of time.
        JavascriptExecutor exec = (JavascriptExecutor) driver;
        exec.executeAsyncScript("window.setTimeout(function(arguments){" + // the functions takes in arguments
                        // passed by webDriver
                        "arguments[0].innerText=arguments[1];" +
                        "arguments[arguments.length-1]();" +
                        "},5000, arguments);",
                heading, "My New Heading Text");

        Assertions.assertEquals("My New Heading Text",
                heading.getText());
    }

    @AfterEach
    public void closeDriver() {
        driver.close();
    }
}