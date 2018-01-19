package io.github.froger.instamaterial.ui.Models;

import java.util.Map;

/**
 * Created by czltd on 1/17/18.
 */

public class LikeUser {

    Map<String, Boolean> isUserLiked;

    public LikeUser() {

    }

    public LikeUser(Map<String, Boolean> isUserLiked) {

        this.isUserLiked = isUserLiked;


    }
}
