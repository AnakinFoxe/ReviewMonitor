package com.anakinfoxe.reviewmonitor.util;

import com.anakinfoxe.reviewmonitor.model.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This crawler will gather all the node id and create category:node mapping
 *
 * Created by xing on 2/20/15.
 */
public class NodeCrawler {

    // CSS Query strings
    private final String Q_LINKS_       = "a.nav_a";

    // Amazon RESTful Service API URL
    private final String URL_BASE_ = "http://www.amazon.com/gp/site-directory/ref=nav_sad";

    // Regular Expression Patterns
    private final Pattern PTN_REF_NODE_ = Pattern.compile("node=([0-9]+)");

    private int maxRetries_ = 10;

    private Node parseNode(Element link) {
        if (link != null) {
            String url = link.attr("href");
            String name = link.ownText().trim();

            if (name.length() > 1) {
                Matcher refNodeMatcher = PTN_REF_NODE_.matcher(url);
                if (refNodeMatcher.find()) {
                    Node nodeObj = new Node();
                    nodeObj.setName(name);
                    nodeObj.setNodeId(refNodeMatcher.group(1));
                    nodeObj.setUpdateDate(new Date());

                    return nodeObj;
                }
            }
        }

        return null;
    }

    private Map<String, Node> scrapePage(String pageUrl) {
        for (int retry = 1; retry <= maxRetries_; ++retry) {
            Map<String, Node> nodeObjs = new HashMap<>();

            try {
                // get the web page source
                Document page = Jsoup.connect(pageUrl).get();

                // get links
                Elements links = page.select(Q_LINKS_);

                // parse each link
                for (Element link : links) {
                    Node nodeObj = parseNode(link);

                    if (nodeObj != null && !nodeObjs.containsKey(nodeObj.getNodeId()))
                        nodeObjs.put(nodeObj.getNodeId(), nodeObj);
                }

                return nodeObjs;
            } catch (IOException e) {
                System.out.println("IOException (NodeCrawler). Retrying "
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

        return new HashMap<>();   // empty
    }

    /**
     * Crawl all the categories with node id
     * @return
     */
    public Map<String, Node> crawl() {
        System.out.println("NodeCrawler initiated...");
        Map<String, Node> nodeObjs = scrapePage(URL_BASE_);
        System.out.println("NodeCrawler finished.");

        return nodeObjs;
    }

    public static void main(String[] args) {
        NodeCrawler nc = new NodeCrawler();

        Map<String, Node> nodeObjs = nc.crawl();

        for (Node nodeObj : nodeObjs.values())
            System.out.println(nodeObj.getName() + ": " + nodeObj.getNodeId());
    }
}
