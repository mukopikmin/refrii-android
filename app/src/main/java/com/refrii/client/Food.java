package com.refrii.client;

import java.util.Date;

/**
 * Created by yusuke on 2017/09/01.
 */

public class Food {
    private int id;
    private String name;
    private String notice;
    private double amount;
    private Date expirationDate;
    private boolean needsAdding;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
    private Unit unit;
    private User createdUser;
    private User updatedUser;
    private Box box;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNotice() {
        return notice;
    }

    public double getAmount() {
        return amount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public boolean isNeedsAdding() {
        return needsAdding;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Unit getUnit() {
        return unit;
    }

    public Box getBox() {
        return box;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountWithUnit() {
        return amount + " " +  unit.getLabel();
    }

    public String getCreatedInfo() {
        return createdUser.getName() + " (" + createdUser.getEmail() + ")\n" +
                createdAt;
    }

    public String getUpdatedInfo() {
        return updatedUser.getName() + " (" + updatedUser.getEmail() + ")\n" +
                updatedAt;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object object) {
        Food food = (Food) object;
        return this.id == food.getId();
    }

    public void decrease(double diff) {
        this.amount -= diff;
        if (this.amount < 0) {
            this.amount = 0;
        }
    }

    public void increase(double diff) {
        this.amount += diff;
    }
}
