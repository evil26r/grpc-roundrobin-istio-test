package com.grpc.test;

import io.grpc.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        Server server = context.getBean("server", Server.class);
        server.awaitTermination();
    }
}
