package com.skysoftatm.bblreactor.ch06;

import com.salesforce.reactorgrpc.GrpcRetry;
import com.skysoftatm.bblreactor.protobuf.types.Altitude;
import com.skysoftatm.bblreactor.protobuf.types.ReactorDemoGrpc;
import com.skysoftatm.bblreactor.protobuf.types.Request;
import com.skysoftatm.bblreactor.protobuf.types.Speed;
import com.skysoftatm.bblreactor.protobuf.types.Tweet;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GrpcDemoClient {
    private static final String FLIGHT = "AF1234";
    private static String HOST = "localhost";
    private static int PORT = 9000;

    // Important: reuse the same channel to avoid opening a server connection for each call
    private static ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
            .usePlaintext().build();


    private static ReactorDemoGrpc.ReactorDemoStub createStub() {
        return ReactorDemoGrpc.newReactorStub(channel);
    }

    private static Flux<Speed> getSpeedFor(String aircraftId) {
        Mono<Request> monoRequest = Mono.just(Request.newBuilder().setAircraftId(aircraftId).build());
        // the type parameter is still necessary for javac, even in Java 11
        return monoRequest.as(GrpcRetry.OneToMany.<Request, Speed>retryImmediately(r -> createStub().speed(r)));
    }

    private static Flux<Altitude> getAltitudeFor(String aircraftId) {
        Mono<Request> monoRequest = Mono.just(Request.newBuilder().setAircraftId(aircraftId).build());
        // the type parameter is still necessary for javac, even in Java 11
        return monoRequest.as(GrpcRetry.OneToMany.<Request, Altitude>retryImmediately(r -> createStub().altitude(r)));
    }

    // Bidirectional streaming
    private static Flux<Tweet> chat(Flux<String> input) {
        Flux<Tweet> inputTweets = input.map(s -> Tweet.newBuilder().setPayload(s).build());
        return inputTweets.compose(GrpcRetry.ManyToMany.<Tweet, Tweet>retryImmediately(r -> createStub().chat(r)));

    }

    public static void main(String[] args) {
        getSpeedFor(FLIGHT).subscribe(System.out::println);
        getAltitudeFor(FLIGHT).doOnNext(System.out::println).blockLast();

//        chat(Flux.interval(ofSeconds(1))
//                .map(String::valueOf))
//                .map(i -> i.getPayload())
//                .doOnNext(System.out::println)
//                .blockLast();

    }

}
