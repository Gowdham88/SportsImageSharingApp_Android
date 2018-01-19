package io.github.froger.instamaterial.ui.Models;

/**
 * Created by czltd on 12/22/17.
 */

public class Users {

    private String email;
    private String profileImageURL;
    private String userName;


    public Users() {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Users(String email, String profileImageURL, String userName) {
        this.email = email;
        this.profileImageURL = profileImageURL;
        this.userName        = userName;

    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getuserName() {
        return userName;
    }


}
