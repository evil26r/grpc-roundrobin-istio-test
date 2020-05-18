package com.grpc.test.service;

import com.grpc.test.config.ClientMessageService;
import com.grpc_http2_test.grpc.MessageGrpc;
import com.grpc_http2_test.grpc.MessageService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

import static com.grpc.test.utils.AddressResolver.LOCAL_ADDRESS;

@Slf4j
@RequiredArgsConstructor
public class ServerMessageService extends MessageGrpc.MessageImplBase {

    private final String responsePayload = LOCAL_ADDRESS;

    private final ClientMessageService clientMessageService;

    @Override
    public void send(MessageService.Payload request, StreamObserver<MessageService.Payload> responseObserver) {
        log.info("Received message: [{}]", request.getPayload());
        String response = clientMessageService.sendMessage(request.getPayload() + " -> " + responsePayload);
        responseObserver.onNext(prepareRequest(response));
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
                                .onNext(prepareRequest(clientMessageService
                                        .sendMessageToStream(value.getPayload() + " -> " + responsePayload, value.getCount()))));
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Encountered error in send stream.", t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }


    private static MessageService.Payload prepareRequest(String payload) {
        return MessageService.Payload.newBuilder().setPayload(payload).build();
    }

}
