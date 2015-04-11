package com.anakinfoxe.reviewmonitor.util;

import com.anakinfoxe.reviewmonitor.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * This crawler will search and grab all the products given manufacturer name
 *
 * Created by xing on 2/20/15.
 */
public class ProductCrawler {

    // Amazon RESTful Service API URL fragments
    // for search
    private final String URL_BASE_      = "http://www.amazon.com";
    private final String URL_REF_       = "/s/ref=nav_sad?ie=UTF8";
    private final String URL_BRAND_     = "&field-brandtextbin=";
    private final String URL_NODE_      = "&node=";
    // for product page
    private final String URL_PRODUCT_   = "/dp/";

    // CSS Query strings
    private final String Q_NEXT_PAGE_   = "pagnNextLink";
    private final String Q_PRODUCT_ID_  = "li[data-asin]";
    private final String Q_MODEL_NUM_   = "b:containsOwn(Item model number:)";


    private int maxRetries_ = 10;


    private String getFirstPageUrl(String brand, String nodeId) {
        return URL_BASE_ + URL_REF_ + URL_BRAND_ + brand + URL_NODE_ + nodeId;
    }

    private String getNextPageUrl(Document page) {
        Element nextPage = page.getElementById(Q_NEXT_PAGE_);

        if (nextPage != null && nextPage.attr("href") != null)
            return URL_BASE_ + nextPage.attr("href");
        else
            return "";  // empty
    }

    private Product parseProduct(String productId) {
        String pageUrl = URL_BASE_ + URL_PRODUCT_ + productId;

        for (int retry = 1; retry <= maxRetries_; ++retry) {
            Product productObj = new Product();

            try {
                // get the web page source
                Document page = Jsoup.connect(pageUrl).get();

                productObj.setProductId(productId);

                Element title = page.getElementsByTag("title").first();
                if (title != null)
                    productObj.setName(title.ownText().trim());

                Element model = page.select(Q_MODEL_NUM_).first();
                if (model != null && model.parent() != null) {
                    String modelNum = model.parent().ownText().trim();
                    modelNum = modelNum.toUpperCase();

                    productObj.setModelNum(modelNum);
                }

                productObj.setUpdateDate(new Date());

                System.out.println("Product " + productId + " obtained");

                return productObj;
            } catch (IOException e) {
                System.out.println("IOException (ProductPage). Retrying "
                        + retry + "/" + maxRetries_
                        + ". " + e.getMessage());

                // incremental waiting
                try {
                    Thread.sleep(3000 + 5000 * retry);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // TODO: when reaches maxRetries, do something?
            }
        }

        return null;
    }

    private Map<String, Product> scrapePage(StringBuffer pageUrl) {
        for (int retry = 1; retry <= maxRetries_; ++retry) {
            Map<String, Product> productObjs = new HashMap<>();

            try {
                // get the web page source
                Document page = Jsoup.connect(pageUrl.toString()).get();

                // get products
                Elements products = page.select(Q_PRODUCT_ID_);

                // parse each product page to get detail info
                for (Element product : products) {
                    String productId = product.attr("data-asin");

                    if (productId != null && !productObjs.containsKey(productId))
                        productObjs.put(productId, parseProduct(productId));
                }

                // update url to the next page url
                pageUrl.delete(0, pageUrl.length());
                pageUrl.append(getNextPageUrl(page));

                return productObjs;
            } catch (IOException e) {
                System.out.println("IOException (ProductSearch). Retrying "
                        + retry + "/" + maxRetries_
                        + ". " + e.getMessage()
                        + ": " + pageUrl);

                if (e.toString().contains("Status=404")) {
                    System.out.println("Amazon does not have such page. Abort");
                    break;
                }

                // incremental waiting
                try {
                    Thread.sleep(3000 + 5000 * retry);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // TODO: when reaches maxRetries, do something?
            }
        }

        return new HashMap<>();   // empty
    }

    /**
     * Crawl products info given brand name and node id
     * @param brand
     * @param nodeId
     * @return
     */
    public Map<String, Product> crawl(String brand, String nodeId) {
        System.out.println("ProductCrawler for [" + brand + "] at node="
                + nodeId + " initiated...");

        Map<String, Product> allProductObjs = new HashMap<>();
        StringBuffer url = new StringBuffer();
        url.append(getFirstPageUrl(brand, nodeId));

        int nPage = 1;
        while (true) {
            Map<String, Product> productObjs = scrapePage(url);

            if (productObjs.size() > 0)
                allProductObjs.putAll(productObjs);

            System.out.println("ProductCrawler for [" + brand + "] at node="
                    + nodeId + " currentSize=" + allProductObjs.size());

            if (url.length() == 0 || productObjs.size() == 0) {
                System.out.println("Done Parsing Product Search at Page " + nPage);
                break;
            }
        }

        System.out.println("ProductCrawler for [" + brand + "] at node="
                + nodeId + " finished. Total=" + allProductObjs.size());

        return allProductObjs;
    }

    public static void main(String[] args) {
        ProductCrawler pc = new ProductCrawler();

        pc.crawl("soundbot", "2335752011");
    }
}
