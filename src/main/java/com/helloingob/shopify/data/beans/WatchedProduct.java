package com.helloingob.shopify.data.beans;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "watched_product", catalog = "shopper")
public class WatchedProduct implements java.io.Serializable {

    private Integer id;
    private Product product;

    public WatchedProduct() {}

    public WatchedProduct(Product product) {
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
