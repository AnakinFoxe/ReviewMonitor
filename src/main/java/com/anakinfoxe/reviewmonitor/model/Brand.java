package com.anakinfoxe.reviewmonitor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xing on 2/16/15.
 */
@Entity
@XmlRootElement
public class Brand {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    public Brand() {

    }

    public Brand(String name) {
        this.name = name;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
