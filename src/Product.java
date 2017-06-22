/**
 * Created by place on 20.06.2017.
 */
public class Product {
    private String name = null;
    private String author = null;
    private Double price = null;
    private String store = null;
    private String link = null;
    private String publisher = null;
    private String type = null;

    public Product(String name, String author, Double price, String store, String link, String publisher, String type)
    {
        this.name = name.trim().toLowerCase();
        this.author = author.trim().toLowerCase();
        this.price = price;
        this.store = store.trim().toLowerCase();
        this.link = link;
        this.publisher = publisher.trim().toLowerCase();
        this.type = type.trim().toLowerCase();
    }

    public String getName() {return name; }

    public String getAuthor() { return author; }

    public Double getPrice() { return price; }

    public String getStore() { return store; }

    public String getLink() { return link; }

    public String getPublisher() { return publisher; }

    public String getType() { return type; }

    public String toString() { return this.store + " - " + this.name + " - " + this.author + " - " + this.price; }
}
