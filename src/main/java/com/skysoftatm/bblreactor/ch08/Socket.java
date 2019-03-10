package com.skysoftatm.bblreactor.ch08;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.function.Function;

import static java.time.Duration.ofSeconds;

public class Socket {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        DisposableServer server = createServer(in -> in.map(i -> "Hello " + i));

        Connection client = createClient(Flux.interval(ofSeconds(1)).map(String::valueOf),
                in -> in.doOnNext(i -> System.out.println("Server sent > " + i)));

        client.onDispose().block();
        server.onDispose().block();
    }

    private static DisposableServer createServer(Function<Flux<String>, Flux<String>> mapper) {
        return TcpServer.create()
                .port(PORT)
                .handle((in, out) -> out
                        .options(o -> o.flushOnEach(true))
                        .sendString(mapper.apply(in.receive().asString()))
                        .then())
                .bindNow();
    }

    private static Connection createClient(Flux<String> outbound, Function<Flux<String>, Flux<String>> mapper) {
        return TcpClient.create()
                .host("localhost")
                .port(PORT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handle((in, out) -> {
                    //Send to server
                    Mono<Void> outboundPublication = out
                            .options(o -> o.flushOnEach(true))
                            .sendString(outbound).then();

                    //Receive from server
                    Mono<Void> inboundStream =
                            mapper.apply(in.receive().asString())
                                    .then();

                    // Combine both streams: Netty will subscribe to both and start the reception & emission
                    return inboundStream.mergeWith(outboundPublication);
                })
                .connectNow();
    }


}
