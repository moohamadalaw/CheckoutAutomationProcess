//package tests;
//
//import base.BaseTest;
//import org.testng.Assert;
//import org.testng.Reporter;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import pages.*;
//import testData.Product;
//import utility.ExcelUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CheckoutTestFeathure extends BaseTest {
//    PageFactoryManager factory;
//
//    HomePage HP;
//    LoginPage LP;
//    ProductDetailesPage PDP;
//    CartPage CP;
//    ShippingPage SP;
//    DelevaryMethodPage DP;
//    PayamentPage PM;
//    OrderConfirmPage OCP;
//    OrderSummaryPage OSP;
//    ReturningCustomerCheckoutPage RCCP;
//
//
//    @BeforeMethod
//    public void setupPage() {
//        factory = new PageFactoryManager(getDriver());
//
//        HP = factory.getHomePage();
//        LP = factory.getLoginPage();
//        PDP = factory.getProductDetailesPage();
//        CP = factory.getCartPage();
//        SP = factory.getShippingPage();
//        DP = factory.getDelevaryMethodPage();
//        PM = factory.getPayamentPage();
//        OCP = factory.getOrderConfirmPage();
//        RCCP = factory.getReturningCustomerCheckoutPage();
//        OSP = factory.getOrderSummaryPage();
//        HP.openHomePage();
//    }
//
//    @DataProvider(name = "checkoutData", parallel = false)
//    public Object[][] getCheckoutData() throws Exception {
//        String path = "src/test/java/testData/CheckoutData.xlsx";
//        String sheetName = "Sheet1";
//
//        ExcelUtils.setExcelFile(path, sheetName);
//        int totalRows = ExcelUtils.getRowCount();
//        int totalCols = 15;
//
//        Object[][] data = new Object[totalRows - 1][totalCols];
//        int dataIndex = 0;
//
//        for (int i = 1; i < totalRows; i++) {
//            String idValue = ExcelUtils.getCellData(i, "ID").trim();
//
//            if (idValue.isEmpty() || idValue.equalsIgnoreCase("skip")) {
//                continue;
//            }
//
//            data[dataIndex][0] = idValue;
//            data[dataIndex][1] = ExcelUtils.getCellData(i, "TestCase_Title");
//            data[dataIndex][2] = ExcelUtils.getCellData(i, "UserType");
//            data[dataIndex][3] = ExcelUtils.getCellData(i, "Email");
//            data[dataIndex][4] = ExcelUtils.getCellData(i, "Password");
//            data[dataIndex][5] = ExcelUtils.getCellData(i, "Products");
//            data[dataIndex][6] = ExcelUtils.getCellData(i, "Qty");
//            data[dataIndex][7] = ExcelUtils.getCellData(i, "fName");
//            data[dataIndex][8] = ExcelUtils.getCellData(i, "lName");
//            data[dataIndex][9] = ExcelUtils.getCellData(i, "StreetAddress");
//            data[dataIndex][10] = ExcelUtils.getCellData(i, "ZIP");
//            data[dataIndex][11] = ExcelUtils.getCellData(i, "phone");
//            data[dataIndex][12] = ExcelUtils.getCellData(i, "CreditCardNumber");
//            data[dataIndex][13] = ExcelUtils.getCellData(i, "ExpiryDate");
//            data[dataIndex][14] = ExcelUtils.getCellData(i, "ExpectedResult");
//
//            dataIndex++;
//        }
//
//        Object[][] finalData = new Object[dataIndex][totalCols];
//        System.arraycopy(data, 0, finalData, 0, dataIndex);
//        return finalData;
//    }
//
//    @Test(dataProvider = "checkoutData")
//    public void runCheckoutTest(String ID, String TestCase_Title, String UserType, String Email, String Password,
//                                String Products, String Qty, String fName, String lName, String StreetAddress,
//                                String ZIP, String phone, String CreditCardNumber, String ExpiryDate,
//                                String ExpectedResult) throws InterruptedException {
//
//
//        Reporter.log("Running Test Case: " + TestCase_Title);
//        boolean checkoutProcess = false;
//        String excelPath = "src/test/java/testData/CheckoutData.xlsx";
//        String sheetName = "Sheet1";
//
//        try {
//            HP.waitHomePageOpen();
//            HP.closeCookieSection();
//
//            if (UserType.equals("Registered")) {
//                LP.loginAsRegisteredUser(Email, Password);
//                HP.goToCart();
//                CP.EmptyTheCart();
//
//                List<Product> expectedProducts = new ArrayList<>();
//                PDP.AddProductsToCart(Products, Qty, expectedProducts);
//
//                HP.goToCart();
//                CP.waitForCartPageOpen();
//
//                List<Product> cartProducts = new ArrayList<>();
//                for (Product expected : expectedProducts) {
//                    Product cartProduct = CP.getCartProductInfoById(expected.getId());
//                    cartProducts.add(cartProduct);
//                }
//
//                Assert.assertTrue(CP.verifyCartTotalMatchesSum(), "Cart total does not match sum of products");
//
//                CP.goTOCheckoutPage();
//                List<Product> summaryProducts = new OrderSummaryPage(getDriver()).getAllOrderSummaryProducts();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "CheckoutPage does not match sum of products");
//
//                for (Product expected : expectedProducts) {
//                    Product cartProduct = cartProducts.stream()
//                            .filter(p -> p.getId().contains(expected.getId()))
//                            .findFirst().orElse(null);
//
//                    Product summaryProduct = summaryProducts.stream()
//                            .filter(p -> p.getId().contains(expected.getId()))
//                            .findFirst().orElse(null);
//
//                    if (cartProduct == null || summaryProduct == null)
//                        throw new AssertionError("Product with ID " + expected.getId() + " not found in one of the pages");
//
//                    boolean sameName = expected.getName().equalsIgnoreCase(cartProduct.getName())
//                            && cartProduct.getName().equalsIgnoreCase(summaryProduct.getName());
//
//                    boolean samePrice = expected.getPrice().equals(cartProduct.getPrice())
//                            && cartProduct.getPrice().equals(summaryProduct.getPrice());
//
//                    if (!sameName || !samePrice)
//                        throw new AssertionError("not match for product ID: " + expected.getId());
//                }
//
//                SP.goToDeliveryMethod();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Delivery Page not match sum of products");
//
//                DP.goToPaymentMethod();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "payment Page not match sum of products");
//
//                getDriver().navigate().refresh();
//                Thread.sleep(15000);
//                PM.setCartNumber(CreditCardNumber);
//                PM.setExp(ExpiryDate);
//                Thread.sleep(2000);
//                PM.placeOrder();
//                checkoutProcess = OCP.isOrderConfirmed();
//
//            } else if (UserType.equals("Guest")) {
//                List<Product> expectedProducts = new ArrayList<>();
//                PDP.AddProductsToCart(Products, Qty, expectedProducts);
//
//                HP.goToCart();
//                CP.waitForCartPageOpen();
//
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Cart Page does not match sum of products");
//
//                List<Product> cartProducts = new ArrayList<>();
//                for (Product expected : expectedProducts) {
//                    Product cartProduct = CP.getCartProductInfoById(expected.getId());
//                    cartProducts.add(cartProduct);
//                }
//
//                CP.goTOCheckoutPage();
//                Assert.assertTrue(CP.verifyCartTotalMatchesSum(), "Checkout Page does not match sum of products");
//
//                RCCP.clickGustUserButton();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Checkout Page does not match sum of products");
//
//                SP.fillShippingInformation(Email, fName, lName, StreetAddress, ZIP, phone);
//                SP.goToDeliveryMethod();
//                SP.UseThisAddrisButtonClick();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "delivery page does not match sum of products");
//
//                DP.goToPaymentMethod();
//                Assert.assertTrue(OSP.verifyTotalMatchesSum(), "Payment Page does not match Total sum of products");
//
//                getDriver().navigate().refresh();
//                Thread.sleep(15000);
//                PM.setCartNumber(CreditCardNumber);
//                PM.setExp(ExpiryDate);
//                Thread.sleep(2000);
//                PM.placeOrder();
//                checkoutProcess = OCP.isOrderConfirmed();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (ExpectedResult.contains("Failure")) {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
//                Assert.assertTrue(true);
//                return;
//            } else if (ExpectedResult.contains("Successful")) {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
//                Assert.fail("Checkout failed! Exception: " + e.getClass().getSimpleName() +
//                        " | Message: " + e.getMessage());
//            }
//        }
//
//        if (ExpectedResult.contains("Successful")) {
//            if (checkoutProcess) {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
//                Assert.assertTrue(true);
//            } else {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
//                Assert.fail("Expected successful checkout but failed.");
//            }
//        } else if (ExpectedResult.contains("Failure")) {
//            if (!checkoutProcess) {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
//            } else {
//                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
//                Assert.fail("Expected failed checkout but succeeded.");
//            }
//        }
//    }
//
//}
//
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
        String sheetName = "Sheet1";

        ExcelUtils.setExcelFile(path, sheetName);
        int totalRows = ExcelUtils.getRowCount();
        int totalCols = 15;

        Object[][] data = new Object[totalRows - 1][totalCols];
        int dataIndex = 0;

        for (int i = 1; i < totalRows; i++) {
            String idValue = ExcelUtils.getCellData(i, "ID").trim();

            if (idValue.isEmpty() || idValue.equalsIgnoreCase("skip")) {
                continue;
            }

            for (int j = 0; j < totalCols; j++) {
                data[dataIndex][j] = ExcelUtils.getCellData(i, j);
            }

            dataIndex++;
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

        test = extent.createTest("Checkout Test - " + TestCase_Title)
                .assignCategory(UserType);

        Reporter.log("Running Test Case: " + TestCase_Title);
        boolean checkoutProcess = false;
        String excelPath = "src/test/java/testData/CheckoutData.xlsx";
        String sheetName = "Sheet1";

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