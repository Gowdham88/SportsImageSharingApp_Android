package com.cz.SarvodayaHBandroid.ui.Models;

/**
 * Created by czltd on 12/22/17.
 */

public class Users {
    private String email;
    private String profileImageURL;
    private String username;

    public Users() {

    }
    public Users(String email, String profileImageURL, String username) {
        this.email = email;
        this.profileImageURL = profileImageURL;
        this.username        = username;

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
