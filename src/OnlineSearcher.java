import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by place on 20.06.2017.
 */

public class OnlineSearcher {
    private static ArrayList<Product> products = new ArrayList<>();

    public OnlineSearcher() {
    }

    private static void searchOnDr(String request) throws IOException {
        Elements rawProducts = null;
        String url = "http://www.dr.com.tr/search?q=" + URLEncoder.encode(request, "utf-8");
        System.out.println(url);
        Document doc = Jsoup.connect(url).get();
        try {
            rawProducts = doc.select(".list-cell");
        } catch (NullPointerException npe) {
            return;
        }
        for (Element e : rawProducts) {
            try {
                String name = e.select("div[class=details]").first().select("a[class=item-name]").first().text();
                String author = e.select("div[class=details]").first().select("a[class=who]").first().text();
                String price = e.select("div[class=details]").select("span[class=price]").first().text().replace(',', '.').split(" ")[0];
                String link = "http://www.dr.com.tr/" + e.select("div[class=details]").first().select("a[class=item-name]").first().attr("href");
                products.add(new Product(name, author, Double.parseDouble(price), "D&R", link));
            } catch (NullPointerException npe) {
                continue;
            }
        }
    }

    private static void searchOnKy(String request) throws IOException {
        Elements rawProducts = null;
        String url = "http://www.kitapyurdu.com/index.php?route=product/search&filter_name=" + URLEncoder.encode(request, "utf-8");
        System.out.println(url);
        Document doc = Jsoup.connect(url).get();
        try {
            rawProducts = doc.select("div[id=product-table]").first().select("div[itemtype=http://schema.org/Book]");
        } catch (NullPointerException npe) {
            return;
        }
        for (Element e : rawProducts) {
            try {
                String name = e.select("img[itemprop=image]").first().attr("alt");
                String author = e.select("span[itemprop=author]").first().select("span[itemprop=name]").text();
                String price = e.select("div[class=price-new]").first().select("span[class=value]").first().text().replace(',', '.');
                String link = e.select("a[itemprop=url]").first().attr("href");
                products.add(new Product(name, author, Double.parseDouble(price), "Kitapyurdu", link));
            } catch (NullPointerException npe) {
                continue;
            }
        }
    }


    private static void searchOnIdefix(String request) throws IOException{
        String url = "http://www.idefix.com/search?q=" + URLEncoder.encode(request, "utf-8");
        Elements rawProducts =null;
        System.out.println(url);
        Document doc = Jsoup.connect(url).get();
        try {
            rawProducts = doc.select("div[class=list-cell]");
        } catch (NullPointerException npe) {
            return;
        }
        for (Element e : rawProducts) {
            try {
                String name = e.select("div[class=summary]").first().select("a[class=item-name]").first().text();
                String author = e.select("a[class=who]").first().text();
                String price = e.select("span[class=price]").first().text().replace(',', '.').split(" ")[0];
               String link = "http://idefix.com" + e.select("a[class=item-name]").first().attr("href");
                products.add(new Product(name, author, Double.parseDouble(price), "Idefix", link));
            } catch (NullPointerException npe) {
                continue;
            }
        }
    }

    public static ArrayList<Product> run(String bookname) throws IOException
    {
        products.clear();
        searchOnIdefix(bookname);
        searchOnKy(bookname);
        searchOnDr(bookname);
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
        for (Product p : products)
            System.out.println(p);
        return products;
    }

    public static void main(String [] args) throws IOException {
        run("deneme");
    }

}
