package com.skysoftatm.bblreactor.ch08;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.function.Function;

import static java.time.Duration.ofSeconds;

/**
 * Be careful, this server is voluntarily simplified, this is still a TCP connection, so nothing guarantees
 * that the server will receive one string for each element emitted by the client.
 * If the server is slow, several stings emitted by the client may be conflated in a single string on the server side.
 */
public class Socket {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        DisposableServer server = createServer();

        Connection client = createClient(Flux.interval(ofSeconds(1)).map(String::valueOf),
                in -> in.doOnNext(i -> System.out.println("Server sent > " + i)));

        client.onDispose().block();
        server.onDispose().block();
    }

    private static DisposableServer createServer() {
        return TcpServer.create()
                .port(PORT)
                .handle((in, out) -> out
                        .options(o -> o.flushOnEach(true))
                        .sendString(transformInput(in)).then())
                .bindNow();
    }

    private static Flux<String> transformInput(NettyInbound in) {
        return in.receive().asString().map(bb -> "Hello " + bb);
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

                    //Receive
                    Mono<Void> inboundStream =
                            mapper.apply(in.receive().asString())
                                    .then();

                    return inboundStream.mergeWith(outboundPublication);
                })
                .connectNow();
    }


}
