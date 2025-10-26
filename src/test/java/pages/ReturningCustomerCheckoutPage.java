package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ReturningCustomerCheckoutPage {
    private WebDriver driver;

    private By gustUserButton =  By.cssSelector("button[data-analytics-name='continue-as-guest']");

    public ReturningCustomerCheckoutPage(WebDriver driver){
        this.driver = driver;
    }

    public void clickGustUserButton(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[data-analytics-name='continue-as-guest']")));
        driver.findElement(gustUserButton).click();
    }

}
