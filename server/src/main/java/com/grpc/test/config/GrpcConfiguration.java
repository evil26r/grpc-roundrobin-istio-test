package com.grpc.test.config;

import com.grpc.test.service.ServerMessageService;
import com.grpc_http2_test.grpc.MessageGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class GrpcConfiguration implements DisposableBean {

    @Value("${grpc.server.service.port}")
    private int port;

    @Value("${grpc.target.address}")
    private String grpcServerAddress;

    private Server server;

    @Bean
    public Server server(ServerMessageService serverMessageService) throws IOException, InterruptedException {
        log.info("Will try start server with port: [{}]", port);
        server = ServerBuilder.forPort(port)
                .addService(serverMessageService)
                .build()
                .start();
        log.info("GRPC server is started");
        return server;
    }

    @Bean
    public Channel channel() {
        log.info("Start creating channel with address: [{}]", grpcServerAddress);
        return ManagedChannelBuilder.forTarget(grpcServerAddress).usePlaintext().build();
    }

    @Bean
    public MessageGrpc.MessageBlockingStub messageBlockingStub(Channel channel) {
        return MessageGrpc.newBlockingStub(channel);
    }

    @Bean
    public MessageGrpc.MessageStub messageStub(Channel channel) {
        return MessageGrpc.newStub(channel);
    }

    @Bean
    public ClientMessageService clientMessageService(MessageGrpc.MessageBlockingStub messageBlockingStub,
                                                     MessageGrpc.MessageStub messageStub) {
        return new ClientMessageService(messageBlockingStub, messageStub);
    }

    @Bean
    public ServerMessageService serverMessageService(ClientMessageService clientMessageService) {
        return new ServerMessageService(clientMessageService);
    }

    @Override
    public void destroy() throws Exception {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        log.info("Server is shutdown");
    }
}
