package com.anakinfoxe.reviewmonitor.model;

/**
 * Created by xing on 2/20/15.
 */
public class Node {

    private Long id;

    private String name;

    private String nodeId;

    public Node() {
    }

    public Node(String name, String nodeId) {
        this.name = name;
        this.nodeId = nodeId;
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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
