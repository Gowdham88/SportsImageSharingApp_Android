package io.github.froger.instamaterial.ui.Models;

/**
 * Created by czltd on 12/22/17.
 */

public class Comment {

    private String profileImageURL;
    private String userName;
    private String uid;
    private String comment;

    public Comment() {

    }

    public Comment(String uid, String profileImageURL, String userName,String comment) {

        this.profileImageURL = profileImageURL;
        this.userName        = userName;
        this.uid             = uid;
        this.comment         = comment;

    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getuserName() {
        return userName;
    }
}
