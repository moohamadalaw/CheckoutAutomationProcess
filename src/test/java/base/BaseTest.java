package base;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeMethod;


public class BaseTest {
    protected WebDriver driver;
    @BeforeMethod
    public void setup(){
        DriverManager.getDriver();
    }
    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }
    @AfterMethod
    public void destructing(){
        DriverManager.quitDriver();
    }


}