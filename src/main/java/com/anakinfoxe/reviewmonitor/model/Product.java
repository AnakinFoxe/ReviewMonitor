package com.anakinfoxe.reviewmonitor.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xing on 2/16/15.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min=1, max=256)
    private String productId;

    @NotNull
    @Size(min=1, max=2048)
    private String name;

    @Size(min=1, max=256)
    private String modelNum;

    // this field is used to determine whether needs to renew reviews
    private Integer numOfReviewsOnPage;

    // this field is used to determine whether needs to renew product info
    private Date updateDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private transient List<Review> reviews = new ArrayList<>();

    @ManyToOne
    private Brand brand;    // owner of OneToMany

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public Integer getNumOfReviewsOnPage() {
        return numOfReviewsOnPage;
    }

    public void setNumOfReviewsOnPage(Integer numOfReviewsOnPage) {
        this.numOfReviewsOnPage = numOfReviewsOnPage;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
