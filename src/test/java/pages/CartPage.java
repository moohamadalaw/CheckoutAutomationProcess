package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;

    private By CheckOutButton = By.className("c-checkout-buttons__checkout");

    public CartPage(WebDriver driver){
        this.driver = driver;
    }



        public boolean isEmptyCartMessageVisible(){

            WebDriverWait shortWait  = new WebDriverWait(driver, Duration.ofSeconds(10));
            boolean isEmptyCartVisible = driver.findElements(
                    By.cssSelector(".cart-empty-text.text-center")
            ).size() > 0;

            if(isEmptyCartVisible) {
                return true;
            }
            else{
                return false;
            }
        }

        public boolean goTOCheckoutPage(){

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".c-checkout-buttons__checkout.u-b1.btn.btn-primary")));
                driver.findElement(CheckOutButton).click();
                return true;
            } catch (Exception e) {

                return false;
            }
    }


    public int getCartItemCount() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            List<WebElement> emptyCart = driver.findElements(By.className("t-cart__empty-cart-contents"));
            if (!emptyCart.isEmpty()) {
                return 0;
            }

            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("order-product-item-container")));
            List<WebElement> products = driver.findElements(By.className("order-product-item-container"));
            int count = products.size();
            return count;

        } catch (Exception e) {
            return 0;
        }
    }


    public void EmptyTheCart(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mainContentTitle")));
        if(getCartItemCount()<1){
            return;
        }

        List<WebElement> products = driver.findElements(By.className("order-product-item-container"));



        System.out.println("Number of products in cart: " + products.size());

        for (WebElement product : products) {
            try {

                WebElement removeButton = product.findElement(By.cssSelector(".c-product-card-actions__remove-btn"));

                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.elementToBeClickable(removeButton));

                removeButton.click();
                Thread.sleep(2000);

            } catch (Exception e) {
            }
        }

    }

    public void waitForCartPageOpen(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mainContentTitle")));
    }

}
