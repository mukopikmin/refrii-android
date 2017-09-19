package com.refrii.client;

import java.util.Date;

/**
 * Created by yusuke on 2017/09/01.
 */

public class Unit {
    private int id;
    private String label;
    private double step;
    private Date createdAt;
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public double getStep() {
        return step;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean equals(Object object) {
        Unit unit = (Unit) object;
        return label.equals(unit.getLabel());
    }
}
