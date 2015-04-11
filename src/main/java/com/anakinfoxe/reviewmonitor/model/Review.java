package com.anakinfoxe.reviewmonitor.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by xing on 2/16/15.
 */
@Entity
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min=1, max=256)
    private String name;

    private Integer rate;

    @Size(min=1, max=2048)
    private String title;

    private Date date;

    @Size(min=1, max=2048)
    private String permalink;

    private Float helpRatio;

    @ManyToOne
    private Product product;    // owner of OneToMany

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
