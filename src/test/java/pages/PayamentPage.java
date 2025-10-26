package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Date;

public class PayamentPage {
    private WebDriver driver;

    private By cartNumber = By.id("account-cc");
    private By exp = By.id("exp-date");

    private By placeOrder = By.className("c-place-order__btn");
    public PayamentPage(WebDriver driver){
        this.driver = driver;
    }

    public void setCartNumber(String cartNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("account-cc")));
        WebElement creditCardField = driver.findElement(By.id("account-cc"));
        creditCardField.clear();
        creditCardField.sendKeys(cartNumber);
    }

    public void setExp(String exp) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exp-date")));
        driver.findElement(this.exp).sendKeys(exp);
    }

    public void placeOrder() throws InterruptedException {
        Thread.sleep(4000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(placeOrder));

        driver.findElement(this.placeOrder).click();
    }



}
