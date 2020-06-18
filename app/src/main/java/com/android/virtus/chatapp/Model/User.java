package com.android.virtus.chatapp.Model;

public class User {
    public String id, name, email, imageURL, status;

    public User() {
    }

    public User(String id, String name, String email, String imageURL, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
