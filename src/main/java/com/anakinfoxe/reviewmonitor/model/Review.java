package com.anakinfoxe.reviewmonitor.model;

import java.util.Date;

/**
 * Created by xing on 2/16/15.
 */
public class Review {

    private Long id;

    private String name;

    private Integer rate;

    private String title;

    private Date date;

    private String permalink;

    private Float helpRatio;

    public Review() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public Float getHelpRatio() {
        return helpRatio;
    }

    public void setHelpRatio(Float helpRatio) {
        this.helpRatio = helpRatio;
    }
}
