package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.time.Duration;

public class ShippingPage {

    private WebDriver driver;
    private By continueToDeliveryMethodButton = By.id("shipping-next-btn");
    private By firstName = By.cssSelector("input[id='fName']");

    private By emailAddres = By.cssSelector("input[id='checkout_step1_email']");

    private By lastName = By.cssSelector("input[id='lName']");

    private By streetAddres = By.cssSelector("input[label='Street Address*']");

    private By zipCode = By.cssSelector("input[id='zipbox']");


    private By phone = By.cssSelector("input[id='phone1box']");

    private By useThisAddrisButton = By.cssSelector(".m-shipping-address-verfication__buttons-container button.btn-primary");







    public ShippingPage(WebDriver driver){
        this.driver = driver;
    }

    public void setEmailAddress(String email) {
        driver.findElement(emailAddres).sendKeys(email);
    }

    public void setFirstName(String fName) {
        driver.findElement(firstName).sendKeys(fName);

    }

    public void setLastName(String lName) {
        driver.findElement(lastName).sendKeys(lName);
    }

    public void setStreetAddress(String address) {
        driver.findElement(streetAddres).sendKeys(address);
    }
    public void setZipCode(String zip) {
        driver.findElement(zipCode).sendKeys(zip);
    }

    public void setPhone(String phoneNumber) {
        driver.findElement(phone).sendKeys(phoneNumber);
    }

    public void fillShippingInformation(String email, String fName, String lName, String address, String zip, String phoneNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordion__content-step1")));


        driver.findElement(emailAddres).sendKeys(email);

        driver.findElement(firstName).sendKeys(fName);

        driver.findElement(lastName).sendKeys(lName);

        driver.findElement(streetAddres).clear();
        driver.findElement(streetAddres).sendKeys(address);

        driver.findElement(zipCode).clear();
        driver.findElement(zipCode).sendKeys(zip);

        driver.findElement(phone).clear();
        driver.findElement(phone).sendKeys(phoneNumber);
    }

    public void UseThisAddrisButtonClick(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("m-shipping-address-verfication__header-bar")));

        driver.findElement(useThisAddrisButton).click();
    }


    public void goToDeliveryMethod(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shipping-next-btn")));

        driver.findElement(continueToDeliveryMethodButton).click();
    }
}