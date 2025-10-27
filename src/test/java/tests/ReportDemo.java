package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.LoginPage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.apache.commons.io.FileUtils;

public class ReportDemo {

    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setup() {
        String path = System.getProperty("user.dir") + "/test-output/LoginReport.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Login Automation Report");
        reporter.config().setDocumentTitle("Selenium + Extent Report");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Mohammad Alawnah");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void testLoginSuccess() {
        test = extent.createTest("Login Test - Valid Information");
        try {
            driver.get("https://certwcs.frontgate.com/?aka_bypass=5C73514EE7A609054D81DE61DD9CA3D6");

            LoginPage LP = new LoginPage(driver);
            HomePage HP = new HomePage(driver);

            LP.loginAsRegisteredUser("malawnah2003@gmail.com", "Mohammad123@");
            HP.hoverOnSignIn();

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".welcome----user-first-name---")));

            Assert.assertTrue(HP.isLoginSuccess(), "Login was not successful");
            test.log(Status.PASS, "User logged in successfully");
        } catch (Exception e) {
            captureScreenshot("LoginSuccess_Failed");
            test.log(Status.FAIL, "Login test failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testLoginFailure() {
        test = extent.createTest("Login Test - Valid Information");
        try {
            driver.get("https://certwcs.frontgate.com/?aka_bypass=5C73514EE7A609054D81DE61DD9CA3D6");

            LoginPage LP = new LoginPage(driver);
            HomePage HP = new HomePage(driver);

            LP.loginAsRegisteredUser("", "wrongPass123@");

            Assert.assertFalse(HP.isLoginSuccess(), "Login should fail with invalid data");
            test.log(Status.PASS, "Login failed as expected (invalid data)");

        } catch (AssertionError ae) {
            captureScreenshot("LoginFailure_Failed");
            test.log(Status.FAIL, "Assertion failed: " + ae.getMessage());
            throw ae;
        } catch (Exception e) {
            captureScreenshot("LoginFailure_Error");
            test.log(Status.FAIL, "Unexpected error: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush();
    }

    private void captureScreenshot(String screenshotName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String folderPath = System.getProperty("user.dir") + "/test-output/screenshots/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = folderPath + screenshotName + ".png";
            File dest = new File(filePath);
            FileUtils.copyFile(src, dest);

            test.addScreenCaptureFromPath("./screenshots/" + screenshotName + ".png");
        } catch (IOException e) {
            test.log(Status.WARNING, "⚠️ Failed to capture screenshot: " + e.getMessage());
        }
    }
}
