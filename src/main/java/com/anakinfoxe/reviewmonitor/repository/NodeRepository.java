package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Node;

import java.util.List;

/**
 * Created by xing on 3/8/15.
 */
public interface NodeRepository {

    Node save(Node node);

    Node saveOrUpdate(Node node);

    Node loadById(Long id);

    List<Node> loadAll();
}
