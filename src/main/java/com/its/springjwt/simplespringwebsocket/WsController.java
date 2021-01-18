package com.its.springjwt.simplespringwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WsController {

//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message) throws Exception {
//
//        Thread.sleep(1000); // simulated delay
//        return new Greeting( HtmlUtils.htmlEscape(message.getName()) + "has just followed you!");
//    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Response response(WsRequest message) throws Exception {

        Thread.sleep(1000); // simulated delay
        return new Response(message);
    }


}
