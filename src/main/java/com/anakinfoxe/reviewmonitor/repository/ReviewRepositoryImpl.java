package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import org.hibernate.ScrollableResults;
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
public class ReviewRepositoryImpl implements ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Review save(Review review) {

        em.persist(review);
        em.flush();

        return review;
    }

    @Override
    public Review saveOrUpdate(Review review) {

        em.merge(review);
        em.flush();

        return review;
    }

    @Override
    public Review loadById(Long id) {

        try {
            String hql = "select g from Review g where g.id = :id";
            Query query = em.createQuery(hql, Review.class);
            query.setParameter("id", id);

            return (Review) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Review> loadAll() {

        try {
            String hql = "select g from Review g";
            Query query = em.createQuery(hql, Review.class);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Review> loadLatestByProduct(Product product, int pageNum, int pageSize) {
        try {
            String hql = "select g from Review g where g.product = :product order by date desc";
            Query query = em.createQuery(hql, Review.class);
            query.setParameter("product", product);
            query.setFirstResult((pageNum - 1) * pageSize);
            query.setMaxResults(pageSize);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Review> loadAllByProduct(Product product) {

        try {
            String hql = "select g from Review g where g.product = :product";
            Query query = em.createQuery(hql, Review.class);
            query.setParameter("product", product);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Review> loadLimitLatestLowestByBrand(Brand brand, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public List<Review> loadAllByBrand(Brand brand) {
        try {
            String hql = "select g from Review g where g.brand = :brand order by g.date desc, g.rate asc";
            Query query = em.createQuery(hql, Review.class);
            query.setParameter("brand", brand);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


}
