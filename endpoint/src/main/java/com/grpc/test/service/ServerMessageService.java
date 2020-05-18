package com.grpc.test.service;

import com.grpc_http2_test.grpc.MessageGrpc;
import com.grpc_http2_test.grpc.MessageService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

import static com.grpc.test.utils.AddressResolver.LOCAL_ADDRESS;

@Slf4j
public class ServerMessageService extends MessageGrpc.MessageImplBase {

    private static final String responsePayload = LOCAL_ADDRESS;

    @Override
    public void send(MessageService.Payload request, StreamObserver<MessageService.Payload> responseObserver) {
        String requestPayload = request.getPayload();
        log.info("Received message: [{}]", requestPayload);
        MessageService.Payload payload = MessageService.Payload.newBuilder().setPayload(requestPayload + " -> " + responsePayload).build();
        responseObserver.onNext(payload);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<MessageService.Payload> sendStream(StreamObserver<MessageService.Payload> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(MessageService.Payload value) {
                log.info("Received message: [{}]", value);
                IntStream.range(0, value.getCount())
                        .forEach(i -> responseObserver
                                .onNext(prepareRequest(value.getPayload() + " -> " + responsePayload)));
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Encountered error in send stream.", t);
            }

            @Override
            public void onCompleted() {
                log.info("Stream is completed");
                responseObserver.onCompleted();
            }
        };
    }

    private static MessageService.Payload prepareRequest(String payload) {
        return MessageService.Payload.newBuilder().setPayload(payload).build();
    }
}
