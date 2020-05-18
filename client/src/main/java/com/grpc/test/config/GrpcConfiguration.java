package com.grpc.test.config;

import com.grpc_http2_test.grpc.MessageGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

    @Value("${grpc.target.address}")
    private String grpcServerAddress;

    @Bean
    public Channel channel() {
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
}
