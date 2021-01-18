package com.its.springjwt.payload.response;

import java.util.ArrayList;
import java.util.List;

public class StatisticsYearResponse {

    private List<Integer> follows;
    private List<Integer> unfollows;

    public  StatisticsYearResponse(){}

    public StatisticsYearResponse(List<Integer> follows, List<Integer> unfollows){
        this.follows = follows;
        this.unfollows = unfollows;
    }

    public List<Integer> getFollows() {
        return follows;
    }

    public void setFollows(List<Integer> follows) {
        this.follows = follows;
    }

    public List<Integer> getUnfollows() {
        return unfollows;
    }

    public void setUnfollows(List<Integer> unfollows) {
        this.unfollows = unfollows;
    }
}
