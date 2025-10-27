package pages;

import org.openqa.selenium.WebDriver;

public class PageFactoryManager {
    private WebDriver driver;

    public PageFactoryManager(WebDriver driver) {
        this.driver = driver;
    }

    public HomePage getHomePage() {
        return new HomePage(driver);
    }

    public LoginPage getLoginPage() {
        return new LoginPage(driver);
    }

    public ProductDetailesPage getProductDetailesPage() {
        return new ProductDetailesPage(driver);
    }

    public CartPage getCartPage() {
        return new CartPage(driver);
    }

    public ShippingPage getShippingPage() {
        return new ShippingPage(driver);
    }

    public DelevaryMethodPage getDelevaryMethodPage() {
        return new DelevaryMethodPage(driver);
    }

    public PayamentPage getPayamentPage() {
        return new PayamentPage(driver);
    }

    public OrderConfirmPage getOrderConfirmPage() {
        return new OrderConfirmPage(driver);
    }

    public ReturningCustomerCheckoutPage getReturningCustomerCheckoutPage() {
        return new ReturningCustomerCheckoutPage(driver);
    }

    public OrderSummaryPage getOrderSummaryPage(){
        return new OrderSummaryPage(driver);
    }
}
