package czsm.github.froger.instamaterial.ui.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by czltd on 12/22/17.
 */

public class Post {

    private String profileImageURL;
    private String userName;
    private String uid;
    private String caption;
    private String postimage;
    private int likecount;
    private Boolean isLiked;
    private long postTime;
    public Map<String, Boolean> userlikes;

    public Post() {

    }

    public Post(String uid, String profileImageURL, String userName,String caption,String postimage,int likecount,Boolean isLiked,long postTime,Map<String, Boolean> userlikes) {

        this.profileImageURL = profileImageURL;
        this.userName        = userName;
        this.caption         = caption;
        this.uid             = uid;
        this.postimage       = postimage;
        this.likecount       = likecount;
        this.isLiked         = isLiked;
        this.postTime        = postTime;
        this.userlikes       = userlikes;

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

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public void setLikecount(Integer likecount) {
        this.likecount = likecount;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public Map<String, Boolean> getUserlikes() {
        return userlikes;
    }

    public void setUserlikes(Map<String, Boolean> userlikes) {
        this.userlikes = userlikes;
    }

    public String getUid() {
        return uid;
    }

    public String getCaption() {
        return caption;
    }

    public long getPostTime() {
        return postTime;
    }

    public String getPostimage() {
        return postimage;
    }

    public int getLikecount() {
        return likecount;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getuserName() {
        return userName;
    }
}
