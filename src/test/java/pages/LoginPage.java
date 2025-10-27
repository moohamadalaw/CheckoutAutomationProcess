package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;


public class LoginPage {
    private WebDriver driver;
    private By email = By.cssSelector("input[id='email']");
    private By password = By.cssSelector("input[id='password']");

    private By login = By.className("login-button");
    private By ErrorEmailMessage = By.id("errorMessage-email");

    public LoginPage(WebDriver driver){
        this.driver = driver;
    }
    public boolean isErrorEmailMessageIsDisplay(){
        return driver.findElement(ErrorEmailMessage).isDisplayed();
    }

    public boolean isEmailVisaple(){
        if(driver.findElement(email).isDisplayed()){
            return true;
        }else {
            return false;
        }
    }
    public void setEmail(String email){
        driver.findElement(this.email).sendKeys(email);
    }

    public void setPassword(String pass){
        driver.findElement(this.password).sendKeys(pass);
    }
    public void submit(){
        driver.findElement(login).click();
    }

    public void waitLoginPageOpen(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
    }
    public void loginAsRegisteredUser(String email, String password) {
        HomePage HP = new HomePage(driver);
        LoginPage LP = new LoginPage(driver);
        HP.hoverOnSignIn();
       // Assert.assertTrue(HP.isLoginButtonIsVisable());
        HP.goToLoginPage();

        LP.waitLoginPageOpen();
        LP.setEmail(email);
        LP.setPassword(password);
        LP.submit();

//        HP.hoverOnSignIn();
//        HP.waitForRegisteredUserWelcomeMessage();

    }
}
