package com.skysoftatm.bblreactor.ch06;

import com.skysoftatm.bblreactor.protobuf.types.Altitude;
import com.skysoftatm.bblreactor.protobuf.types.ReactorDemoGrpc;
import com.skysoftatm.bblreactor.protobuf.types.Request;
import com.skysoftatm.bblreactor.protobuf.types.Speed;
import com.skysoftatm.bblreactor.protobuf.types.Tweet;
import org.lognet.springboot.grpc.GRpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@GRpcService
public class GrpcDemoService extends ReactorDemoGrpc.DemoImplBase {

    @Override
    public Flux<Speed> speed(Mono<Request> request) {
        System.out.println("received speed request");
        return request.flatMapMany(r -> Flux.interval(Duration.ofSeconds(1))
                .map(i -> Speed.newBuilder()
                        .setAircraftId(r.getAircraftId())
                        .setSpeed(i)
                        .build()));
    }

    @Override
    public Flux<Altitude> altitude(Mono<Request> request) {
        System.out.println("received altitude request");
        return request.flatMapMany(r -> Flux.interval(Duration.ofSeconds(1))
                .map(i -> Altitude.newBuilder()
                        .setAircraftId(r.getAircraftId())
                        .setAltitude(i)
                        .build()));
    }

    @Override
    public Flux<Tweet> chat(Flux<Tweet> request) {
        System.out.println("received chat request");
        return request.map(i -> Tweet.newBuilder()
                .setTimestamp(i.getTimestamp())
                .setPayload("Hello " + i.getPayload()).build());
    }
}
