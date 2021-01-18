package com.its.springjwt.simplespringwebsocket;

public class WsRequest {

    private String sender;
    private String target;

    public WsRequest() {
    }

    public WsRequest(String sender, String target) {

        this.sender = sender;
        this.target = target;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }
}