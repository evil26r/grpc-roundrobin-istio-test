package com.grpc.test.config;


import com.grpc_http2_test.grpc.MessageGrpc;
import com.grpc_http2_test.grpc.MessageService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public class ClientMessageService {

    private final MessageGrpc.MessageBlockingStub messageBlockingStub;
    private final MessageGrpc.MessageStub messageStub;

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public String sendMessage(String payload) {
        log.info("Will try to send message with payload: [{}].", payload);
        MessageService.Payload request = MessageService.Payload.newBuilder().setPayload(payload).build();
        return messageBlockingStub.send(request).getPayload();
    }

    public String sendMessageToStream(String payload, int count) {
        log.info("Will try to send message with payload: [{}].", payload);
        StreamObserver<MessageService.Payload> requestObserver = messageStub.sendStream(new StreamObserver<>() {
            @SneakyThrows
            @Override
            public void onNext(MessageService.Payload value) {
                log.debug("Get request: [{}]", value.getPayload());
                queue.put(value.getPayload());
            }

            @SneakyThrows
            @Override
            public void onError(Throwable t) {
                log.debug("Get throwable: [{}]", t.getMessage());
                queue.put(t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Stream is complete");
            }
        });

        return IntStream.range(0, count)
                .mapToObj(operand -> prepareRequest(payload, count))
                .peek(requestObserver::onNext)
                .flatMap($payload ->
                    IntStream.range(0, count)
                            .mapToObj(value -> {
                                try {
                                    return queue.poll(1, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    return null;
                                }
                            })
                )
                .collect(Collectors.joining());
    }

    private static MessageService.Payload prepareRequest(String payload, int count) {
        return MessageService.Payload.newBuilder().setPayload(payload).setCount(count).build();
    }
}