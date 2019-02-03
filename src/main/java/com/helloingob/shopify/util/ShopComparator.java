package com.helloingob.shopify.util;

import java.util.Comparator;

import com.helloingob.shopify.data.beans.Order;
import com.helloingob.shopify.data.beans.Shop;

public class ShopComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        Shop shop1 = o1.getShop();
        Shop shop2 = o2.getShop();

        int cmp = 0;
        if (shop1 == null) {
            if (shop2 != null) {
                cmp = 1;
            } else {
                cmp = 0;
            }
        } else if (shop2 == null) {
            cmp = -1;
        } else {
            cmp = shop1.getTitle().compareTo(shop2.getTitle());
        }
        return cmp;
    }

}
