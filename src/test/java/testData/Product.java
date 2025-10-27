package testData;

public class Product {
    private String id;
    private String name;
    private String price;

    public Product(String name, String price ,String id) {
        this.name = name;
        this.price = price;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
    public String getId(){
        return id;
    }

    public boolean equals(Product other) {
        return this.name.equals(other.name) && this.price.equals(other.price);
    }

    @Override
    public String toString() {
        return "Product{name='" + name + "', price='" + price + "'}";
    }
}
