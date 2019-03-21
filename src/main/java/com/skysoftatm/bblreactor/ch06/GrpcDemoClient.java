package com.skysoftatm.bblreactor.ch06;

import com.salesforce.reactorgrpc.GrpcRetry;
import com.skysoftatm.bblreactor.protobuf.types.Altitude;
import com.skysoftatm.bblreactor.protobuf.types.ReactorDemoGrpc;
import com.skysoftatm.bblreactor.protobuf.types.Request;
import com.skysoftatm.bblreactor.protobuf.types.Speed;
import com.skysoftatm.bblreactor.protobuf.types.Tweet;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;

public class GrpcDemoClient {
    private static final String FLIGHT = "AF1234";
    private static String HOST = "localhost";
    private static int PORT = 9000;

    // Important: reuse the same channel to avoid opening a server connection for each call
    private static ManagedChannel channel = ManagedChannelBuilder
            .forTarget("localhost:9000,localhost:9001")
            .nameResolverFactory(new TestResolverProvider())
            .defaultLoadBalancingPolicy("round_robin") // that's the only implementation provided with pick_first
            .usePlaintext().build();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            getSpeedFor(FLIGHT).doOnNext(System.out::println).take(1).blockLast();
        }


        //getAltitudeFor(FLIGHT).doOnNext(System.out::println).blockLast();

//        chat(Flux.interval(ofMillis(10))
//                .map(String::valueOf))
//                .map(i -> i.getPayload()+" - round trip in "+timeDifference(i)+" ms")
//                .doOnNext(System.out::println)
//                .blockLast();

    }

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
        Flux<Tweet> inputTweets = input.map(s -> Tweet.newBuilder().setTimestamp(System.currentTimeMillis()).setPayload(s).build());
        return inputTweets.compose(GrpcRetry.ManyToMany.<Tweet, Tweet>retryImmediately(r -> createStub().chat(r)));

    }

    private static class TestResolverProvider extends NameResolver.Factory {

        public NameResolver newNameResolver(URI targetUri, NameResolver.Helper helper) {
            return new NameResolver() {
                @Override
                public String getServiceAuthority() {
                    return "custom";
                }

                @Override
                public void start(Listener listener) {
                    //TODO in reality we would need to parse the targetUri to extract these 2 addresses
                    InetSocketAddress add1 = new InetSocketAddress("localhost", 9001);
                    InetSocketAddress add2 = new InetSocketAddress("localhost", 9000);
                    EquivalentAddressGroup equivalentAddressGroup1 = new EquivalentAddressGroup(Arrays.asList(add1));
                    EquivalentAddressGroup equivalentAddressGroup2 = new EquivalentAddressGroup(Arrays.asList(add2));
                    listener.onAddresses(Arrays.asList(equivalentAddressGroup1, equivalentAddressGroup2), Attributes.EMPTY);
                }

                @Override
                public void shutdown() {

                }
            };
        }

        @Override
        public String getDefaultScheme() {
            return "test";
        }
    }

    private static long timeDifference(Tweet t) {
        return System.currentTimeMillis() - t.getTimestamp();
    }

}
