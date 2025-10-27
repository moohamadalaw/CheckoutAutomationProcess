package pages;

import com.sun.tools.jconsole.JConsoleContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import testData.Product;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;

    private By CheckOutButton = By.className("c-checkout-buttons__checkout");
    public CartPage(WebDriver driver){
        this.driver = driver;
    }

    public Product getCartProductInfoById(String expectedId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("order-product-item-container")));

        List<WebElement> items = driver.findElements(By.className("order-product-item-container"));

        for (WebElement item : items) {
            String id = item.findElement(By.cssSelector(".c-product-details__item-number"))
                    .getText()
                    .replace("#", "")
                    .trim();


            if (id.contains(expectedId)) {
                String name = item.findElement(By.cssSelector(".c-product-card__title.family-base")).getText().trim();
                String price = item.findElement(By.cssSelector(".price__integer")).getText().trim();
                return new Product(name, price, id);
            }
        }

        throw new RuntimeException("Product with ID '" + expectedId + "' not found in cart!");
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

    public boolean verifyCartTotalMatchesSum() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("order-product-item-container")));

        List<WebElement> items = driver.findElements(By.className("order-product-item-container"));
        double calculatedTotal = 0.0;

        for (WebElement item : items) {
            String priceText = item.findElement(By.cssSelector(".price__integer")).getText().trim();
            String qtyText = "1";
            List<WebElement> qtyElements = item.findElements(By.xpath(".//*[contains(text(),'Qty')]"));
            if (!qtyElements.isEmpty()) {
                qtyText = qtyElements.get(0).getText().replaceAll("\\D", "").trim();
            }

            double price = Double.parseDouble(priceText.replaceAll("[^0-9.]", ""));
            int quantity = Integer.parseInt(qtyText.isEmpty() ? "1" : qtyText);
            calculatedTotal += (price * quantity);
        }

        double handlingFee = 0.0, shipping = 0.0, tax = 0.0;

        List<WebElement> handlingEl = driver.findElements(By.cssSelector(".c-order-summary__special-handling span:last-child"));
        if (!handlingEl.isEmpty()) {
            String handlingText = handlingEl.get(0).getText().trim();
            if (handlingText.contains("$")) handlingFee = Double.parseDouble(handlingText.replaceAll("[^0-9.]", ""));
        }

        List<WebElement> shippingEl = driver.findElements(By.cssSelector(".c-order-summary__shipping-value"));
        if (!shippingEl.isEmpty()) {
            String shippingText = shippingEl.get(0).getText().trim();
            if (shippingText.contains("$")) shipping = Double.parseDouble(shippingText.replaceAll("[^0-9.]", ""));
        }

        List<WebElement> taxEl = driver.findElements(By.xpath("//div[contains(@class,'c-order-summary__shipping-tax')]//span[last()]"));
        if (!taxEl.isEmpty()) {
            String taxText = taxEl.get(0).getText().trim();
            if (taxText.contains("$")) tax = Double.parseDouble(taxText.replaceAll("[^0-9.]", ""));
        }

        double expectedTotal = calculatedTotal + handlingFee + shipping + tax;

        WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".c-order-summary__estimated-total span:last-child")));

        String totalText = totalElement.getText().trim().replaceAll("[^0-9.]", "");
        double displayedTotal = Double.parseDouble(totalText);

        return Math.abs(expectedTotal - displayedTotal) < 0.01;
    }


}
