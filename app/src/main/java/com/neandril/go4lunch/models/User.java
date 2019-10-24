package com.neandril.go4lunch.models;

public class User {
    private String uid;
    private String username;
    private String userEmail;
    private String urlProfilePicture;

    public User() { }

    public User(String uid, String username, String userEmail, String urlProfilePicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.urlProfilePicture = urlProfilePicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUrlProfilePicture() {
        return urlProfilePicture;
    }

    public void setUrlProfilePicture(String urlProfilePicture) {
        this.urlProfilePicture = urlProfilePicture;
    }
}
