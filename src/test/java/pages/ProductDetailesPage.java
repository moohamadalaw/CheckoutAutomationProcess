package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ProductDetailesPage   {

    private WebDriver driver;

    private By productName = By.className("t-universal-product-details-heading-info__title");
    private By productPrice = By.className("price");
    private By productImage = By.className("c-image");
    private By addToCartButton = By.className("c-universal-add-to-cart");


    private  By options = By.cssSelector(".c-universal-options__option-swatch-container");



    public void clickButtonRequiredOptionGroups() {
        By optionGroups = By.cssSelector(".c-universal-options__option");

        List<WebElement> groups = driver.findElements(optionGroups);

        for (int i = 0; i < groups.size(); i++) {
            WebElement group = groups.get(i);
            try {
                if (group.isDisplayed()) {
                    WebElement firstDiv = group.findElement(By.cssSelector(".c-universal-options__option-swatch-container"));

                    WebElement button = firstDiv.findElement(By.tagName("button"));

                    if (button.isDisplayed() && button.isEnabled()) {
                        button.click();
                        Thread.sleep(2000);
                    }

                }
            } catch (Exception e) {
            }
        }
    }

    public void selectFirstOptionFromEachGroup() {
        By optionGroups = By.cssSelector(".c-universal-options__option");

        List<WebElement> groups = driver.findElements(optionGroups);


        for (WebElement group : groups) {
            try {
                WebElement firstOption = null;

                List<WebElement> clickableOptions = group.findElements(By.cssSelector(
                        "button, .c-universal-options__option-swatch-container, img, div"));

                for (WebElement option : clickableOptions) {
                    if (option.isDisplayed() && option.isEnabled()) {
                        firstOption = option;
                        break;
                    }
                }

                if (firstOption != null) {
                    firstOption.click();
                } else {
                }

            } catch (Exception e) {
            }
        }
    }


    public ProductDetailesPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getProductName() {
        return driver.findElement(productName).getText();
    }

    public String getProductPrice() {
        return driver.findElement(productPrice).getText();
    }

    public boolean isProductNameDisaple() {
        return driver.findElement(productName).isDisplayed();
    }

    public boolean isProductPriceDisaple() {
        return driver.findElement(productPrice).isDisplayed();
    }

    public boolean isProductImageDisaple() {
        return driver.findElement(productImage).isDisplayed();
    }

    public void clickAddToCart() {
        driver.findElement(addToCartButton).click();
    }

    public void addToCart() throws InterruptedException{
        clickAddToCart();
    }

    public void viewCart(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("c-items-added-to-cart")));

        driver.findElement(By.className("view-cart-btn")).click();

    }
    public void waitForAddToCartReady() throws InterruptedException {

        WebElement addToCart = driver.findElement(addToCartButton);
        if (addToCart.isDisplayed()) {
            clickButtonRequiredOptionGroups();
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("c-total-universal-price")));


         addToCart = driver.findElement(addToCartButton);

        if (addToCart.isDisplayed()) {
            clickButtonRequiredOptionGroups();
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.elementToBeClickable(By.className("c-universal-add-to-cart")));

    }

    public void verifyAddToCartProduct(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("c-items-added-to-cart")));
    }


    public void AddProductsToCart(String Products,String Qty) throws InterruptedException {


        Products = Products.replace("[", "").replace("]", "").trim();

        if (Products.isEmpty() || Products.equals("") || Products.equalsIgnoreCase("null")) {
            return;
        }


        String[] productIDs = Products.split(",");
        String[] quantities = Qty.split(",");

        if (productIDs.length == 0 || (productIDs.length == 1 && productIDs[0].trim().isEmpty())) {
            return;
        }


        for (int i = 0; i < productIDs.length; i++) {
            String productId = productIDs[i].trim();

            String quantity;
            if (quantities.length == 1) {
                quantity = quantities[0].trim();
            } else if (quantities.length > i) {
                quantity = quantities[i].trim();
            } else {
                quantity = "1";
            }

            if(productIDs.length<1){
                return;
            }


            HomePage HP = new HomePage(driver);
            HP.search(productId);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".text-uppercase.c-universal-add-to-cart.is--large-btn.is--quaternary-btn.btn.btn-primary")
            ));



            waitForAddToCartReady();
            addToCart();
            wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".c-button.c-icon-label-button.c-custom-sheet__close-button")));
            driver.findElement(By.cssSelector(".c-button.c-icon-label-button.c-custom-sheet__close-button")).click();
        }


    }


}