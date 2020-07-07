package _11_js_abstraction.end;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitingForJSAbstractionTest {

    WebDriver driver;

    @BeforeAll
    public static void setupDriver(){
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void waitingJSExample(){

        driver = new ChromeDriver();

        driver.get("https://eviltester.github.io/synchole/messages.html");

        GenericJSWaiter wait = new GenericJSWaiter(driver,20);
        wait.forJSConditionToEvaluateTo(
                "window.liveMessages==0 && window.totalRequestsMade>0", true);

        // get total messages and we will compare at end
        final Long totalRequestsSent = (Long) ((JavascriptExecutor) driver).
                executeScript(
                        "return window.totalRequestsMade");


        wait.forJSConditionToEvaluateTo(
                "window.totalMessagesReceived>0 && window.renderingQueueCount==0",
                true);

        // get total messages and we will compare at end
        final Long totalMessagesReceived = (Long) ((JavascriptExecutor) driver).
                executeScript(
                        "return window.totalMessagesReceived");

        final Long totalDisplayMessages = (Long) ((JavascriptExecutor) driver).
                executeScript(
                        "return window.allMessages.length");

        Assertions.assertEquals(
                totalDisplayMessages,
                (totalRequestsSent*2)+totalMessagesReceived);

    }

    @AfterEach
    public void closeDriver(){
        driver.close();
    }

    private class GenericJSWaiter {
        private final WebDriverWait wait;

        public GenericJSWaiter(WebDriver driver, int timeout) {
            this.wait = new WebDriverWait(driver, timeout);
        }

        public void forJSConditionToEvaluateTo(String javascript, boolean value) {
            wait.until(EvaluateBooleanJSCondition(javascript, value));
        }

        private ExpectedCondition<Boolean> EvaluateBooleanJSCondition(
                String javascript, boolean value) {
            return new ExpectedCondition<Boolean>() {
                @NullableDecl
                @Override
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    try {
                        Boolean jsvalue = (Boolean) ((JavascriptExecutor) webDriver).
                                executeScript(
                                        "return " + javascript
                                );
                        return jsvalue == value;
                    }catch (JavascriptException e){
                        throw new RuntimeException("Exception evaluating - return "
                                + javascript, e);
                    }
                }
            };
        }
    }
}
