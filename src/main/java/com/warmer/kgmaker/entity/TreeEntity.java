package com.warmer.kgmaker.entity;

import java.util.HashMap;
import java.util.List;

public class TreeEntity {

    private String id;

    private String label;

    private List<HashMap<String, Object>> children;

    public TreeEntity() {
    }

    public TreeEntity(String id, String label) {
        this.id = id;
        this.label = label;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
