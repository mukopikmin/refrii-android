package com.refrii.client;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Box implements Serializable {
    private int id;
    private String name;
    private String notice;
    private String imageUrl;
    private boolean isInvited;
    private Date updatedAt;
    private Date createdAt;
    private List<Food> foods;
    private List<User> invitedUsers;
    private User owner;

    public Box(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNotice() {
        return notice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<User> getInvitedUsers() {
        return invitedUsers;
    }

    public User getOwner() {
        return owner;
    }

    public boolean equals(Object object) {
        Box box = (Box) object;
        return box.id == id;
    }
}
