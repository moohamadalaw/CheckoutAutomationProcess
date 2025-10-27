package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import testData.Product;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryPage {
    private WebDriver driver;



    private By subtotal = By.cssSelector(".c-order-summary_subtotal.c-is-row span:last-child");
    private By tax = By.cssSelector(".c-order-summary_shipping-tax.c--is-row span:last-child");

    public OrderSummaryPage(WebDriver driver){
        this.driver = driver;
    }
    public List<Product> getAllOrderSummaryProducts() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".c-order-summary-products-list__card")));

        List<WebElement> productCards = driver.findElements(By.cssSelector(".c-order-summary-products-list__card"));
        List<Product> products = new ArrayList<>();

        for (WebElement card : productCards) {
            try {
                String name = card.findElement(By.cssSelector(".c-order-summary-products-list__card-details-title"))
                        .getText().trim();

                String id = card.findElement(By.cssSelector(".c-order-summary-products-list__card-details span"))
                        .getText().replace("#", "").trim();

                String price = card.findElement(By.cssSelector(".price__integer"))
                        .getText().trim();

                products.add(new Product(name, price, id));

            } catch (Exception e) {
                System.out.println("Failed to extract one product data: " + e.getMessage());
            }
        }

        return products;
    }

    public boolean verifyTotalMatchesSum() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".c-order-summary-products-list__card")));

        List<WebElement> items = driver.findElements(By.cssSelector(".c-order-summary-products-list__card"));
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

        double shipping = 0.0, tax = 0.0;

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

        double expectedTotal = calculatedTotal + shipping + tax;

        WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".c-order-summary__estimated-total span:last-child")));

        String totalText = totalElement.getText().trim().replaceAll("[^0-9.]", "");
        double displayedTotal = Double.parseDouble(totalText);

        return Math.abs(expectedTotal - displayedTotal) < 0.01;
    }


}
