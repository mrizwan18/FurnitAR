package com.razi.furnitar;

import android.net.Uri;

public class user {
    Uri photo;
    private String name;
    private String id;
    private String email;

    public user() {
    }

    public user(String name, String id, String email, Uri photo) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }
}
