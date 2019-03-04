package com.skysoftatm.bblreactor.ch00;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class Example {

    public static void main(String[] args) {
        WebClient client = WebClient.create("http://localhost:8080");
        client.get()

                .uri("/events")

                .accept(MediaType.TEXT_EVENT_STREAM)

                .exchange()

                //.flatMapMany(cr -> cr.bodyToFlux(Stocks.class)) //TODO find some free websocket service

                // batch + average

                .subscribe(System.out::println);
    }
}
