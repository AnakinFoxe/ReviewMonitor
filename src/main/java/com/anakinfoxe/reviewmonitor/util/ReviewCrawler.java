package com.anakinfoxe.reviewmonitor.util;

import com.anakinfoxe.reviewmonitor.model.Review;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This crawler will gather all the review information with given product id
 *
 * Created by xing on 2/16/15.
 */
public class ReviewCrawler {

    /**
     * Information need to be crawled:
     *  Name (from Permalink)
     *  Rate
     *  Title
     *  Date
     *  Permalink
     *  Ratio of Helpfulness (might not exist)
     */

    // CSS Query strings
    private final String Q_REVIEWS_     = "div.a-section.review"; //"div[style=margin-left:0.5em;]";
    private final String Q_RATE_        = "span.a-icon-alt"; //"span:containsOwn(out of 5 stars)";
    private final String Q_TITLE_       = "a.review-title"; //"span[style=vertical-align:middle;] > b";
    private final String Q_DATE_        = "span.review-date";
//    private final String Q_PERMALINK_   = "div.a-section.review";//"a:containsOwn(Permalink)";
    private final String Q_HELP_RATIO_  = "span.review-votes"; //"span:containsOwn(people found the following review helpful)";

    // Amazon RESTful Service API URL fragments
    private final String URL_BASE_      = "http://www.amazon.com/product-reviews/";
    private final String URL_REF_       = "/ref=cm_cr_pr_btm_link_";//"/ref=cm_cr_pr_top_link_next_";
    private final String URL_PAGE_NUM_  = "?ie=UTF8&pageNumber=";
//    private final String URL_REST_      = "&showViewpoints=0&sortBy=bySubmissionDateDescending";
    private final String URL_PERMALINK_ = "http://www.amazon.com/review/";

    // Regular Expression Patterns
    //private final Pattern PTN_RATE_     = Pattern.compile("([\\d]).0 out of 5 stars");
    private final Pattern PTN_HELP_     = Pattern.compile("([\\d]+) of ([\\d]+) people found the following review helpful");
    private final Pattern PTN_NAME_     = Pattern.compile("http://www.amazon.com/review/([a-zA-Z0-9]+)");
    private final Pattern PTN_DATE_     = Pattern.compile("on (.+)");

    // formatter for string -> date conversion
    private DateFormat fmt_ = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

    private int maxRetries_ = 10;

    public ReviewCrawler() {

    }

//    private String getFirstPageUrl(String productId) {
//        return URL_BASE_ + productId;
//    }

    private String getPageUrl(String productId, Integer pageId) {
        StringBuffer sb = new StringBuffer();
        sb.append(URL_BASE_);
        sb.append(productId);
        sb.append(URL_REF_);
        sb.append(pageId.toString());
        sb.append(URL_PAGE_NUM_);
        sb.append(pageId.toString());
//        sb.append(URL_REST_);

        return sb.toString();
    }

    private Review parseReview(Element review) {
        Review reviewObj = new Review();

        // find elements using selector
        Element rate = review.select(Q_RATE_).first();
        Element title = review.select(Q_TITLE_).first();
        Element date = review.select(Q_DATE_).first();
//        Element permalink = review.select(Q_PERMALINK_).first();
        Element helpRatio = review.select(Q_HELP_RATIO_).first();

        // convert string description of rate into integer
        if (rate != null) {
            //Matcher rateMatcher = PTN_RATE_.matcher(rate.ownText());
            //if (rateMatcher.find())
                //reviewObj.setRate(Integer.parseInt(rateMatcher.group(1)));
            reviewObj.setRate(Integer.parseInt(rate.ownText().trim()));
        }

        if (title != null)
            reviewObj.setTitle(title.ownText().trim());

        if (date != null)
            try {
                Matcher dateMatcher = PTN_DATE_.matcher(date.ownText().trim());

                if (dateMatcher.find())
                    reviewObj.setDate(fmt_.parse(dateMatcher.group(1)));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        reviewObj.setName(review.id());
        reviewObj.setPermalink(URL_PERMALINK_ + review.id());
//        if (permalink != null) {
//            String link = permalink.attr("href");
//            reviewObj.setPermalink(link);
//
            // parse name of the review from its permalink
//            Matcher nameMatcher = PTN_NAME_.matcher(link);
//            if (nameMatcher.find())
//                reviewObj.setName(nameMatcher.group(1));
//        }

        if (helpRatio != null) {
            Matcher helpMatcher = PTN_HELP_.matcher(helpRatio.ownText());
            if (helpMatcher.find()) {
                float numerator = Float.parseFloat(helpMatcher.group(1));
                float denominator = Float.parseFloat(helpMatcher.group(2));
                reviewObj.setHelpRatio(numerator / denominator);
            }
        }

        return reviewObj;
    }

    private Map<String, Review> scrapePage(String pageUrl) {
        for (int retry = 1; retry <= maxRetries_; ++retry) {
            Map<String, Review> reviewObjs = new HashMap<>();

            try {
                // get the web page source
                Document page = Jsoup.connect(pageUrl).get();

                // get reviews
                Elements reviews = page.select(Q_REVIEWS_);

                // parse each review
                for (Element review : reviews) {
                    Review reviewObj = parseReview(review);

                    if (reviewObj.getName() != null)
                        reviewObjs.put(reviewObj.getName(), reviewObj);
                }

                return reviewObjs;
            } catch (IOException e) {
                System.out.println("IOException (ReviewPage). Retrying "
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
     * Crawl all the reviews with given product id
     * @param productId
     * @return
     */
    public Map<String, Review> crawl(String productId) {
        Map<String, Review> allReviewObjs = new HashMap<>();
//        String firstPageUrl = getFirstPageUrl(productId);

        // starts with first page URL
//        String url = firstPageUrl;

        int nPage = 1;
        while (true) {
            System.out.println("[" + productId + "] Parsing Review Page " + nPage + "...");
            String url = getPageUrl(productId, nPage);

            Map<String, Review> reviewObjs = scrapePage(url);

            if (reviewObjs.size() > 0) {
                // add to list
                allReviewObjs.putAll(reviewObjs);

                // generate URL for next page
                url = getPageUrl(productId, ++nPage);
            }
            else {
                System.out.println("[" + productId + "] Done Parsing Reviews at Page " + nPage);
                break;
            }
        }

        return allReviewObjs;
    }


    public static void main(String[] args) throws IOException {

        ReviewCrawler rc = new ReviewCrawler();

        rc.crawl("B00IGUUYTI");


    }
}
