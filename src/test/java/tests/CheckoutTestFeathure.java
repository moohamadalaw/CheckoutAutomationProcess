package tests;

import base.BaseTest;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
import pages.*;
import testData.Product;
import utility.ExcelUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutTestFeathure extends BaseTest {
    PageFactoryManager factory;

    HomePage HP;
    LoginPage LP;
    ProductDetailesPage PDP;
    CartPage CP;
    ShippingPage SP;
    DelevaryMethodPage DP;
    PayamentPage PM;
    OrderConfirmPage OCP;
    OrderSummaryPage OSP;
    ReturningCustomerCheckoutPage RCCP;

    ExtentReports extent;
    ExtentTest test;

    @BeforeSuite
    public void setupReport() {
        String path = System.getProperty("user.dir") + "/test-output/CheckoutReport.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Checkout Automation Report");
        reporter.config().setDocumentTitle("E-Commerce Checkout Report");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Mohammad Alawnah");
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }

    @BeforeMethod
    public void setupPage() {
        factory = new PageFactoryManager(getDriver());

        HP = factory.getHomePage();
        LP = factory.getLoginPage();
        PDP = factory.getProductDetailesPage();
        CP = factory.getCartPage();
        SP = factory.getShippingPage();
        DP = factory.getDelevaryMethodPage();
        PM = factory.getPayamentPage();
        OCP = factory.getOrderConfirmPage();
        RCCP = factory.getReturningCustomerCheckoutPage();
        OSP = factory.getOrderSummaryPage();
        HP.openHomePage();
    }

    @DataProvider(name = "checkoutData", parallel = false)
    public Object[][] getCheckoutData() throws Exception {
        String path = "src/test/java/testData/CheckoutData.xlsx";

        ExcelUtils.setExcelFile(path, "new");
        int totalRows = ExcelUtils.getRowCount();
        int totalCols = 15;

        Object[][] data = new Object[totalRows - 1][totalCols];
        int dataIndex = 0;

        for (int i = 1; i < totalRows; i++) {
            String idValue = ExcelUtils.getCellData(i, "ID").trim();
            if (idValue.isEmpty() || idValue.equalsIgnoreCase("skip")) continue;

            String testCaseTitle = ExcelUtils.getCellData(i, "TestCase_Title");
            String userID = ExcelUtils.getCellData(i, "UserID");
            String productInfoID = ExcelUtils.getCellData(i, "ProductInformationId");
            String shippingID = ExcelUtils.getCellData(i, "ShipingInfoID");
            String paymentID = ExcelUtils.getCellData(i, "Payament");
            String expectedResult = ExcelUtils.getCellData(i, "ExpectedResult");

            ExcelUtils.setExcelFile(path, "UserData");
            String userType = ExcelUtils.getCellDataByColumnValue("UserID", userID, "UserType");
            String email = ExcelUtils.getCellDataByColumnValue("UserID", userID, "Email");
            String password = ExcelUtils.getCellDataByColumnValue("UserID", userID, "Password");

            ExcelUtils.setExcelFile(path, "ProductsInformation");
            String products = ExcelUtils.getCellDataByColumnValue("ProductInformationId", productInfoID, "ProductsID");
            String qty = ExcelUtils.getCellDataByColumnValue("ProductInformationId", productInfoID, "Quantity");

            String fName = "", lName = "", address = "", zip = "", phone = "";
            if (shippingID != null && !shippingID.equals("-") && !shippingID.trim().isEmpty()) {
                ExcelUtils.setExcelFile(path, "ShipingInformation");
                fName = ExcelUtils.getCellDataByColumnValue("SHIID", shippingID, "fName");
                lName = ExcelUtils.getCellDataByColumnValue("SHIID", shippingID, "lName");
                address = ExcelUtils.getCellDataByColumnValue("SHIID", shippingID, "StreetAddress");
                zip = ExcelUtils.getCellDataByColumnValue("SHIID", shippingID, "ZIP");
                phone = ExcelUtils.getCellDataByColumnValue("SHIID", shippingID, "phone");
            }

            ExcelUtils.setExcelFile(path, "Payament");
            String card = ExcelUtils.getCellDataByColumnValue("PayamentId", paymentID, "CreditCardNumber");
            String expiry = ExcelUtils.getCellDataByColumnValue("PayamentId", paymentID, "ExpiryDate");

            data[dataIndex] = new Object[]{
                    idValue, testCaseTitle, userType, email, password,
                    products, qty, fName, lName, address, zip, phone,
                    card, expiry, expectedResult
            };

            dataIndex++;
            ExcelUtils.setExcelFile(path, "new");
        }

        Object[][] finalData = new Object[dataIndex][totalCols];
        System.arraycopy(data, 0, finalData, 0, dataIndex);
        return finalData;
    }

    @Test(dataProvider = "checkoutData")
    public void runCheckoutTest(String ID, String TestCase_Title, String UserType, String Email, String Password,
                                String Products, String Qty, String fName, String lName, String StreetAddress,
                                String ZIP, String phone, String CreditCardNumber, String ExpiryDate,
                                String ExpectedResult) throws InterruptedException {

        System.out.println("==============================================");
        System.out.println("🔹 ID: " + ID);
        System.out.println("🔹 TestCase_Title: " + TestCase_Title);
        System.out.println("🔹 UserType: " + UserType);
        System.out.println("🔹 Email: " + Email);
        System.out.println("🔹 Password: " + Password);
        System.out.println("🔹 Products: " + Products);
        System.out.println("🔹 Qty: " + Qty);
        System.out.println("🔹 fName: " + fName);
        System.out.println("🔹 lName: " + lName);
        System.out.println("🔹 StreetAddress: " + StreetAddress);
        System.out.println("🔹 ZIP: " + ZIP);
        System.out.println("🔹 phone: " + phone);
        System.out.println("🔹 CreditCardNumber: " + CreditCardNumber);
        System.out.println("🔹 ExpiryDate: " + ExpiryDate);
        System.out.println("🔹 ExpectedResult: " + ExpectedResult);
        System.out.println("==============================================");

        test = extent.createTest("Checkout Test - " + TestCase_Title)
                .assignCategory(UserType);

        Reporter.log("Running Test Case: " + TestCase_Title);
        boolean checkoutProcess = false;
        String excelPath = "src/test/java/testData/CheckoutData.xlsx";
        String sheetName = "new";

        try {
            HP.waitHomePageOpen();
            HP.closeCookieSection();
            test.log(Status.INFO, "Home page loaded successfully.");

            if (UserType.equals("Registered")) {
                test.log(Status.INFO, "Logging in as Registered user: " + Email);
                LP.loginAsRegisteredUser(Email, Password);
                HP.goToCart();
                CP.EmptyTheCart();

                List<Product> expectedProducts = new ArrayList<>();
                PDP.AddProductsToCart(Products, Qty, expectedProducts);
                test.log(Status.PASS, "Products added to cart successfully.");

                HP.goToCart();
                CP.waitForCartPageOpen();

                List<Product> cartProducts = new ArrayList<>();
                for (Product expected : expectedProducts) {
                    Product cartProduct = CP.getCartProductInfoById(expected.getId());
                    cartProducts.add(cartProduct);
                }

                Assert.assertTrue(CP.verifyCartTotalMatchesSum(), "Cart total does not match sum of products");
                test.log(Status.PASS, "Cart total verified successfully.");

                CP.goTOCheckoutPage();
                List<Product> summaryProducts = new OrderSummaryPage(getDriver()).getAllOrderSummaryProducts();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "CheckoutPage does not match sum of products");
                test.log(Status.PASS, "Checkout page verified successfully.");

                for (Product expected : expectedProducts) {
                    Product cartProduct = cartProducts.stream()
                            .filter(p -> p.getId().contains(expected.getId()))
                            .findFirst().orElse(null);

                    Product summaryProduct = summaryProducts.stream()
                            .filter(p -> p.getId().contains(expected.getId()))
                            .findFirst().orElse(null);

                    if (cartProduct == null || summaryProduct == null)
                        throw new AssertionError("Product with ID " + expected.getId() + " not found in one of the pages");

                    boolean sameName = expected.getName().equalsIgnoreCase(cartProduct.getName())
                            && cartProduct.getName().equalsIgnoreCase(summaryProduct.getName());

                    boolean samePrice = expected.getPrice().equals(cartProduct.getPrice())
                            && cartProduct.getPrice().equals(summaryProduct.getPrice());

                    if (!sameName || !samePrice)
                        throw new AssertionError("Not match for product ID: " + expected.getId());
                }

                SP.goToDeliveryMethod();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Delivery Page not match sum of products");
                DP.goToPaymentMethod();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Payment Page not match sum of products");
                test.log(Status.PASS, "Delivery & Payment pages verified successfully.");

                getDriver().navigate().refresh();
                Thread.sleep(15000);
                PM.setCartNumber(CreditCardNumber);
                PM.setExp(ExpiryDate);
                Thread.sleep(2000);
                PM.placeOrder();
                checkoutProcess = OCP.isOrderConfirmed();
                test.log(Status.PASS, "Order placed successfully.");

            } else if (UserType.equals("Guest")) {
                System.out.println("zip : "+ZIP);
                test.log(Status.INFO, "Running Guest checkout flow for user: " + Email);
                List<Product> expectedProducts = new ArrayList<>();
                PDP.AddProductsToCart(Products, Qty, expectedProducts);

                HP.goToCart();
                CP.waitForCartPageOpen();
                Assert.assertTrue(CP.verifyCartTotalMatchesSum(), "Cart Page does not match sum of products");

                CP.goTOCheckoutPage();
                RCCP.clickGustUserButton();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Checkout Page does not match sum of products");

                SP.fillShippingInformation(Email, fName, lName, StreetAddress, ZIP, phone);
                SP.goToDeliveryMethod();
                SP.UseThisAddrisButtonClick();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Delivery page does not match sum of products");

                DP.goToPaymentMethod();
                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Payment Page does not match Total sum of products");
                test.log(Status.PASS, "Guest flow verified successfully.");

                getDriver().navigate().refresh();
                Thread.sleep(15000);
                PM.setCartNumber(CreditCardNumber);
                PM.setExp(ExpiryDate);
                Thread.sleep(2000);
                PM.placeOrder();
                checkoutProcess = OCP.isOrderConfirmed();
                test.log(Status.PASS, "Guest order placed successfully.");
            }

        } catch (Exception e) {
//            test.log(Status.FAIL, "Exception: " + e.getMessage());
//            captureScreenshot("Checkout_Failed_" + ID);
//            e.printStackTrace();
            if (ExpectedResult.contains("Failure")) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
                test.log(Status.PASS, "Expected failure occurred (negative case).");
                Assert.assertTrue(true);
                return;
            } else if (ExpectedResult.contains("Successful")) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                test.log(Status.FAIL, "Checkout failed unexpectedly.");
                e.printStackTrace();
                Assert.fail("Checkout failed! Exception2: " + e.toString());
                Assert.fail("Checkout failed! Exception2: " + e.getMessage());

            }
        }

        if (ExpectedResult.contains("Successful")) {
            if (checkoutProcess) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
                test.log(Status.PASS, "Checkout completed successfully.");
            } else {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                test.log(Status.FAIL, "Checkout expected success but failed.");
                Assert.fail("Expected successful checkout but failed.");
            }
        } else if (ExpectedResult.contains("Failure")) {
            if (!checkoutProcess) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
                test.log(Status.PASS, "Checkout failed as expected.");
            } else {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                test.log(Status.FAIL, "Checkout succeeded unexpectedly.");
                Assert.fail("Expected failed checkout but succeeded.");
            }
        }
    }

    private void captureScreenshot(String screenshotName) {
        try {
            File src = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
            String folderPath = System.getProperty("user.dir") + "/test-output/screenshots/";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String filePath = folderPath + screenshotName + ".png";
            File dest = new File(filePath);
            FileUtils.copyFile(src, dest);
            test.addScreenCaptureFromPath("./screenshots/" + screenshotName + ".png");
        } catch (IOException e) {
            test.log(Status.WARNING, "Screenshot capture failed: " + e.getMessage());
        }
    }
}