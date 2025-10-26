package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utility.ExcelUtils;

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
    ReturningCustomerCheckoutPage RCCP;

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

            data[dataIndex][0] = idValue;
            data[dataIndex][1] = ExcelUtils.getCellData(i, "TestCase_Title");
            data[dataIndex][2] = ExcelUtils.getCellData(i, "UserType");
            data[dataIndex][3] = ExcelUtils.getCellData(i, "Email");
            data[dataIndex][4] = ExcelUtils.getCellData(i, "Password");
            data[dataIndex][5] = ExcelUtils.getCellData(i, "Products");
            data[dataIndex][6] = ExcelUtils.getCellData(i, "Qty");
            data[dataIndex][7] = ExcelUtils.getCellData(i, "fName");
            data[dataIndex][8] = ExcelUtils.getCellData(i, "lName");
            data[dataIndex][9] = ExcelUtils.getCellData(i, "StreetAddress");
            data[dataIndex][10] = ExcelUtils.getCellData(i, "ZIP");
            data[dataIndex][11] = ExcelUtils.getCellData(i, "phone");
            data[dataIndex][12] = ExcelUtils.getCellData(i, "CreditCardNumber");
            data[dataIndex][13] = ExcelUtils.getCellData(i, "ExpiryDate");
            data[dataIndex][14] = ExcelUtils.getCellData(i, "ExpectedResult");

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

        Reporter.log("Running Test Case: " + TestCase_Title);
        boolean checkoutProcess = false;

        String excelPath = "src/test/java/testData/CheckoutData.xlsx";
        String sheetName = "Sheet1";

        try {
            HP.waitHomePageOpen();
            HP.closeCookieSection();

            if (UserType.equals("Registered")) {
                System.out.println("Cridit Card Number is : "+CreditCardNumber);
                LP.loginAsRegisteredUser(Email, Password);
                HP.goToCart();
                CP.EmptyTheCart();
                PDP.AddProductsToCart(Products, Qty);
                HP.goToCart();
                CP.goTOCheckoutPage();
                SP.goToDeliveryMethod();
                DP.goToPaymentMethod();
                getDriver().navigate().refresh();
                Thread.sleep(15000);
                PM.setCartNumber(CreditCardNumber);
                PM.setExp(ExpiryDate);
                Thread.sleep(2000);
                PM.placeOrder();
                checkoutProcess = OCP.isOrderConfirmed();
            } else if (UserType.equals("Guest")) {
                PDP.AddProductsToCart(Products, Qty);
                HP.goToCart();
                CP.goTOCheckoutPage();
                RCCP.clickGustUserButton();
                SP.fillShippingInformation(Email, fName, lName, StreetAddress, ZIP, phone);
                SP.goToDeliveryMethod();
                SP.UseThisAddrisButtonClick();
                DP.goToPaymentMethod();
                getDriver().navigate().refresh();
                Thread.sleep(15000);
                PM.setCartNumber(CreditCardNumber);
                PM.setExp(ExpiryDate);
                Thread.sleep(2000);
                PM.placeOrder();
                checkoutProcess = OCP.isOrderConfirmed();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (ExpectedResult.contains("Failure")) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
                Assert.assertTrue(true);
                return;
            } else if (ExpectedResult.contains("Successful")) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                Assert.fail("Checkout failed! Exception: " + e.getClass().getSimpleName() +
                        " | Message: " + e.getMessage());
            }
        }

        if (ExpectedResult.contains("Successful")) {
            if (checkoutProcess) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
                Assert.assertTrue(true);
            } else {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                Assert.fail("Expected successful checkout but failed.");
            }
        } else if (ExpectedResult.contains("Failure")) {
            if (!checkoutProcess) {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Pass");
            } else {
                ExcelUtils.setResultByTestId(excelPath, sheetName, ID, "ActualResult", "Fail");
                Assert.fail("Expected failed checkout but succeeded.");
            }
        }
    }
}