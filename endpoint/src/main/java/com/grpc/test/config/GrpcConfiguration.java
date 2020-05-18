package com.grpc.test.config;

import com.grpc.test.service.ServerMessageService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GrpcConfiguration implements DisposableBean {

    @Value("${grpc.server.service.port}")
    private int port;

    private Server server;

    @Bean
    public Server server() throws IOException {
        log.info("Will try start server with port: [{}]", port);
        server = ServerBuilder.forPort(port)
                .addService(new ServerMessageService())
                .build()
                .start();
        log.info("GRPC server is started");
        return server;
    }

    @Override
    public void destroy() throws Exception {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        log.info("Server is shutdown");
    }
}
