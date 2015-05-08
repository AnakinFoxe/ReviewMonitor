package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Node;
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
public class NodeRepositoryImpl implements NodeRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Node save(Node node) {

        em.persist(node);
        em.flush();

        return node;
    }

    @Override
    public Node saveOrUpdate(Node node) {

        em.merge(node);
        em.flush();

        return node;
    }

    @Override
    public Node loadById(Long id) {

        try {
            String hql = "select g from Node g where g.id = :id";
            Query query = em.createQuery(hql, Node.class);
            query.setParameter("id", id);

            return (Node) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Node> loadAll() {

        try {
            String hql = "select g from Node g";
            Query query = em.createQuery(hql, Node.class);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
