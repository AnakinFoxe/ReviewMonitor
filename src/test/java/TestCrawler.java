import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            Map<String, Review> reviews = null;
            try {
                reviews = rc.crawl(productId);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    private static Map<String, String> getDupliates() {
        Map<String, String> contents = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/test/DupResolveList.txt"));
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

    private static boolean testDupResolveCrawler() {
        // construct test list
        Map<String, String> contents = getDupliates();

        // instantiate DupResolveCrawler
        DupResolveCrawler cc = new DupResolveCrawler();

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

    private static Map<String, Review.Status> getStatus() {
        Map<String, Review.Status> status = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/test/StatusList.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] items = line.trim().split(",");
                    int s = Integer.parseInt(items[1]);
                    status.put(items[0], Review.Status.values()[s]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    private static boolean testStatusCrawler() {
        // construct test list
        Map<String, Review.Status> status = getStatus();

        // instantiate StatusCrawler
        StatusCrawler sc = new StatusCrawler();

        // Customer Service names
        Set<String> monitored = new HashSet<>();
        monitored.add("Support Volcus");
        monitored.add("Support Customer Service");

        int correct = 0;
        for (String url : status.keySet()) {
            Review.Status obtainedStatus = sc.crawl(url, monitored);

            if (obtainedStatus.equals(status.get(url)))
                correct++;

        }

        if (correct != status.size())
            return false;
        else
            return true;
    }


    public static void main(String[] args) {
        if (testNodeCrawler())
            System.out.println("NodeCrawler passed");

        if (testProductCrawler("soundbot", "2335752011", 126))
            System.out.println("ProductCrawler passed");

        if (testReviewCrawler())
            System.out.println("ReviewCrawler passed");

        if (testDupResolveCrawler())
            System.out.println("DupResolveCrawler passed");

        if (testStatusCrawler())
            System.out.println("StatusCrawler passed");
    }
}
