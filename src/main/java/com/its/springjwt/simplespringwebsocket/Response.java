package com.its.springjwt.simplespringwebsocket;

public class Response {

    private WsRequest content;

    public Response() {
    }

    public Response(WsRequest content) {
        this.content = content;
    }

    public WsRequest getContent() {
        return content;
    }

}