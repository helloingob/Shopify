package com.helloingob.shopify.data.beans;

import static javax.persistence.GenerationType.IDENTITY;
import java.time.LocalDateTime;

// Generated 12.04.2018 09:30:02 by Hibernate Tools 5.2.8.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Order generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "order", catalog = "shopper")
public class Order implements java.io.Serializable {

    private Integer id;
    private Product product;
    private Shop shop;
    private int amount = 1;
    private LocalDateTime created;
    private LocalDateTime deleted;

    public Order() {
    }

    public Order(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public Order(Product product, Shop shop, int amount) {
        this.product = product;
        this.shop = shop;
        this.amount = amount;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Column(name = "amount", nullable = false)
    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Column(name = "created")
    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Column(name = "deleted")
    public LocalDateTime getDeleted() {
        return this.deleted;
    }

    public void setDeleted(LocalDateTime deleted) {
        this.deleted = deleted;
    }
    
}
