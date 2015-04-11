package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by xing on 3/9/15.
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Product save(Product product) {

        em.persist(product);
        em.flush();

        return product;
    }

    @Override
    public Product saveOrUpdate(Product product) {

        em.merge(product);
        em.flush();

        return product;
    }

    @Override
    public Product loadById(Long id) {

        try {
            String qStr = "select g from Product g where g.id = :id";
            Query query = em.createQuery(qStr, Product.class);
            query.setParameter("id", id);

            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Product loadByProductId(String productId) {
        try {
            String qStr = "select g from Product g where g.productId = :productId";
            Query query = em.createQuery(qStr, Product.class);
            query.setParameter("productId", productId);

            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Product> loadAll() {

        try {
            String qStr = "select g from Product g";
            Query query = em.createQuery(qStr, Product.class);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
