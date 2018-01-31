package com.cz.SarvodayaHBandroid.ui.Models;

import java.util.Map;

/**
 * Created by czltd on 12/22/17.
 */

public class Post {

    private String profileImageURL;
    private String userName;
    private String uid;
    private String caption;
    private String photoURL;
    private int likeCount;
    private Boolean isLiked;
    private long postTime;
    public Map<String, Boolean> likes;

    public Post() {

    }

    public Post(String uid, String profileImageURL, String userName,String caption,String photoURL,int likeCount,Boolean isLiked,long postTime,Map<String, Boolean> likes) {

        this.profileImageURL = profileImageURL;
        this.userName        = userName;
        this.caption         = caption;
        this.uid             = uid;
        this.photoURL       = photoURL;
        this.likeCount       = likeCount;
        this.isLiked         = isLiked;
        this.postTime        = postTime;
        this.likes       = likes;

    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }




}
