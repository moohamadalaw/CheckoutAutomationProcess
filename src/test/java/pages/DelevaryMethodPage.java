package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DelevaryMethodPage {
    private WebDriver driver;
    private By PayamentButtonPage = By.id("nextBtn");

    public DelevaryMethodPage(WebDriver driver){
        this.driver = driver;
    }

    public void goToPaymentMethod(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nextBtn")));
        driver.findElement(PayamentButtonPage).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("account-cc")));
    }

}
