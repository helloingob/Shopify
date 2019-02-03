package com.helloingob.shopify.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helloingob.shopify.data.beans.Order;
import com.helloingob.shopify.data.beans.Product;
import com.helloingob.shopify.data.beans.Shop;
import com.helloingob.shopify.data.beans.WatchedProduct;
import com.helloingob.shopify.util.HibernateUtil;

public class ProductService {
    private static ProductService instance;

    private ProductService() {}

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    /* Method to READ all the orders */
    @SuppressWarnings("unchecked")
    public synchronized List<Order> getOrders() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Order> orders = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            orders = session.createQuery("FROM Order WHERE deleted IS NULL ORDER BY CASE WHEN shop_id IS NULL THEN 1 ELSE 0 END, shop_id").list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return orders;
    }

    /* Method to READ all the products */
    @SuppressWarnings("unchecked")
    public synchronized List<Product> getProducts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Product> products = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            products = session.createQuery("FROM Product").list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return products;
    }

    /* Method to READ all the watched products */
    @SuppressWarnings("unchecked")
    public synchronized List<WatchedProduct> getWatchedProducts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<WatchedProduct> watchedProducts = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            watchedProducts = session.createQuery("FROM WatchedProduct").list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return watchedProducts;
    }

    /* Method to UPDATE for an order */
    public synchronized boolean updateOrder(Order order) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Order updateOrder = (Order) session.get(Order.class, order.getId());
            updateOrder.setAmount(order.getAmount());
            updateOrder.setShop(order.getShop());
            session.update(updateOrder);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    /* Method to READ all the shops */
    @SuppressWarnings("unchecked")
    public synchronized List<Shop> getShops() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Shop> shops = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            shops = session.createQuery("FROM Shop").list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return shops;
    }

    /* Method to CHECK if product already exists and returns id */
    private synchronized Product existsProduct(String productTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Product product = (Product) session.createQuery("FROM Product WHERE lower(title) = :productTitle").setParameter("productTitle", productTitle.toLowerCase()).uniqueResult();
            transaction.commit();
            return product;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    /* Method to CHECK if watched product already exists */
    private synchronized boolean existsWatchedProduct(Product product) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            boolean result = session.createQuery("FROM WatchedProduct WHERE product_id = :productId").setParameter("productId", product.getId()).uniqueResult() != null;
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    /* Method to DELETE an order from the records */
    public synchronized boolean deleteOrder(Integer orderId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Order order = (Order) session.get(Order.class, orderId);
            order.setDeleted(LocalDateTime.now());
            session.update(order);
            //session.delete(order);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    /* Method to DELETE a watched product from the records */
    public synchronized boolean deleteWatchedProduct(Integer productId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            WatchedProduct watchedProduct = (WatchedProduct) session.get(WatchedProduct.class, productId);
            session.delete(watchedProduct);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    /* Method to CREATE an order in the database */
    public synchronized void addOrder(Order order) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Order existingOrder = (Order) session.createQuery("FROM Order WHERE product_id = :productId").setParameter("productId", order.getProduct().getId()).uniqueResult();
            if (existingOrder != null) {
                existingOrder.setAmount(existingOrder.getAmount() + 1);
                session.update(existingOrder);
            } else {
                session.save(order);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to CREATE a product in the database */
    public synchronized Product addProduct(String productTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        Product product = existsProduct(productTitle);
        if (product == null) {
            try {
                transaction = session.beginTransaction();
                Product newProduct = new Product(productTitle);
                session.save(newProduct);
                transaction.commit();
                return newProduct;
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }
            return null;
        } else {
            return product;
        }
    }

    /* Method to CREATE a watched product in the database */
    public synchronized void addWatchedProduct(String productTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        //add product or return existing
        Product product = addProduct(productTitle);

        if (!existsWatchedProduct(product)) {
            //if no duplicate
            try {
                transaction = session.beginTransaction();
                session.save(new WatchedProduct(product));
                transaction.commit();
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
    }

}
