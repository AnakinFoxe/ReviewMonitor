package com.anakinfoxe.reviewmonitor.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by xing on 5/10/15.
 */
@XmlRootElement
public class Crawler {

    String brand;
    boolean isCrawling;
    boolean isRunning;

    public Crawler() {
        this.brand = "NO BRAND";
        this.isCrawling = false;
        this.isRunning = false;
    }

    public Crawler(String brand) {
        this.brand = brand;
        this.isCrawling = false;
        this.isRunning = true;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isCrawling() {
        return isCrawling;
    }

    public void setIsCrawling(boolean isCrawling) {
        this.isCrawling = isCrawling;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
