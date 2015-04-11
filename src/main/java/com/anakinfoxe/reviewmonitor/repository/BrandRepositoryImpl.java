package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Brand;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by xing on 3/11/15.
 */
@Repository
public class BrandRepositoryImpl implements BrandRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Brand save(Brand brand) {

        em.persist(brand);
        em.flush();

        return brand;
    }

    @Override
    public Brand loadById(Long id) {
        try {
            String qStr = "select g from Brand where g.id = :id";
            Query query = em.createQuery(qStr, Brand.class);
            query.setParameter("id", id);

            return (Brand) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Brand loadByName(String name) {
        try {
            String qStr = "select g from Brand where g.name = :name";
            Query query = em.createQuery(qStr, Brand.class);
            query.setParameter("name", name);

            return (Brand) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Brand> loadAll() {
        try {
            String qStr = "select g from Brand";
            Query query = em.createQuery(qStr, Brand.class);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
