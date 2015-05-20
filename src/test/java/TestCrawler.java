import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.util.ContentCrawler;
import com.anakinfoxe.reviewmonitor.util.NodeCrawler;
import com.anakinfoxe.reviewmonitor.util.ProductCrawler;
import com.anakinfoxe.reviewmonitor.util.ReviewCrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xing on 3/1/15.
 */
public class TestCrawler {

    private static final Map<String, Node> nodeObjs = new HashMap<>();
    private static final Map<String, Product> productObjs = new HashMap<>();

    private static Map<String, Node> getNodeObjs(String type) {
        Map<String, Node> nodeObjs = new HashMap<>();

        try {
            FileReader fr = new FileReader("src/test/NodeList.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.trim().split(",");
                if (items[0].equals(type))
                    nodeObjs.put(items[1], new Node(items[2], items[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return nodeObjs;
    }

    private static boolean testNodeCrawler() {
        // construct test list
        Map<String, Node> checkObjs = getNodeObjs("1");
        Map<String, Node> docObjs = getNodeObjs("0");

        // obtain node list from Amazon
        NodeCrawler nc = new NodeCrawler();
        nodeObjs.putAll(nc.crawl());

        // compare
        int correct = 0;
        int incorrect = 0;
        for (Node check : checkObjs.values())
            if (nodeObjs.containsKey(check.getNodeId()))
                ++correct;
        for (Node doc : docObjs.values())
            if (nodeObjs.containsKey(doc.getNodeId()))
                ++incorrect;

        if (correct != checkObjs.size() || incorrect != 0) {
            System.out.println("Node List Obtained Error");
            System.out.println("correct = " + correct + ", incorrect = " + incorrect);
            return false;
        }

        return true;
    }

    private static Map<String, Product> getProductObjs(String brand) {
        Map<String, Product> productObjs = new HashMap<>();

        try {
            FileReader fr = new FileReader("src/test/ProductList.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.trim().split(",");
                if (items[0].equals(brand)) {
                    Product productObj = new Product();
                    productObj.setProductId(items[1]);

                    if (items.length > 2)
                        productObj.setModelNum(items[2]);

                    productObjs.put(items[1], productObj);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return productObjs;
    }

    private static boolean testProductCrawler(String brand, String nodeId, int size) {
        // construct test list
        Map<String, Product> checkObjs = getProductObjs(brand);

        // obtain products from Amazon
        ProductCrawler pc = new ProductCrawler();
        productObjs.putAll(pc.crawl(brand, nodeId));

        int correct = 0;
        for (Product check : checkObjs.values()) {
            String productId = check.getProductId();
            if (productObjs.containsKey(productId)) {
                String productModelNum = productObjs.get(productId).getModelNum();
                String checkModelNum = check.getModelNum();
                if (checkModelNum != null && productModelNum != null
                        && checkModelNum.equals(productModelNum))
                    ++correct;
                else if (checkModelNum == null && productModelNum == null)
                    ++correct;
            }
        }

        if (correct != checkObjs.size() || productObjs.size() != size) {
            System.out.println("Product List Obtained Error");
            System.out.println("correct = " + correct + ", total = " + productObjs.size());
            return false;
        }

        return true;
    }

    private static Map<String, Integer> getReviewNum() {
        Map<String, Integer> reviewNum = new HashMap<>();

        try {
            FileReader fr = new FileReader("src/test/ReviewList.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] items = line.trim().split(",");
                    reviewNum.put(items[0], Integer.parseInt(items[1]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reviewNum;
    }

    private static boolean testReviewCrawler() {
        // construct test list
        Map<String, Integer> reviewNum = getReviewNum();

        // instantiate ReviewCrawler
        ReviewCrawler rc = new ReviewCrawler();

        int correct = 0;
        for (String productId : reviewNum.keySet()) {
            Map<String, Review> reviews = rc.crawl(productId);

            if (reviews.size() == reviewNum.get(productId))
                ++correct;
            else {
                System.out.print("Product " + productId + " reviews obtained error. ");
                System.out.println("Expected: " + reviewNum.get(productId)
                                + " Obtained: " + reviews.size());
            }
        }

        if (correct != reviewNum.size()) {
            System.out.println("Review List Obtained Error.");
            System.out.println("correct = " + correct + " total = " + reviewNum.size());
            return false;
        }

        return true;
    }

    private static Map<String, String> getContents() {
        Map<String, String> contents = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/test/ContentList.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] items = line.trim().split(",");
                    contents.put(items[0], items[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }

    private static boolean testContentCrawler() {
        // construct test list
        Map<String, String> contents = getContents();

        // instantiate ContentCrawler
        ContentCrawler cc = new ContentCrawler();

        int correct = 0;
        for (String url : contents.keySet()) {
            String productId = cc.crawl(url);
            if (contents.get(url).equals(productId)) {
                correct++;
            }
            else {
                System.out.print("Product " + contents.get(url) + " reviews obtained error. ");
                System.out.println("Expected: " + contents.get(url)
                        + " Obtained: " + productId);
            }

        }

        if (correct != contents.size()) {
            System.out.println("Content List Obtained Error.");
            System.out.println("correct = " + correct + " total = " + contents.size());
            return false;
        }


        return true;
    }

    public static void main(String[] args) {
//        if (testNodeCrawler() == true)
//            System.out.println("NodeCrawler passed");
//
//        if (testProductCrawler("soundbot", "2335752011", 126) == true)
//            System.out.println("ProductCrawler passed");
//
//        if (testReviewCrawler() == true)
//            System.out.println("ReviewCrawler passed");

        if (testContentCrawler() == true)
            System.out.println("ContentCrawler passed");
    }
}
