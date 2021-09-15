package com.cz.SarvodayaHBandroid.ui.Models;

/**
 * Created by czltd on 12/22/17.
 */

public class Comment {

    private String profileImageURL;
    private String userName;
    private String uid;
    private String postid;
    private String commentText;
    private long postTime;


    public Comment() {

    }
    public Comment(String uid, String profileImageURL, String userName, String commentText, String postid, long postTime) {

        this.profileImageURL = profileImageURL;
        this.userName        = userName;
        this.uid             = uid;
        this.commentText         = commentText;
        this.postid          = postid;
        this.postTime           =postTime;

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

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }



}
