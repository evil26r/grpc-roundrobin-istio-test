package com.grpc.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final ClientMessageService clientMessageService;

    @GetMapping("rpc")
    public Mono<String> rpc(@RequestParam(name = "payload", defaultValue = "very important payload") String payload,
                            @RequestParam(name = "count", defaultValue = "1") int count) {
        return Mono.from(Flux.range(0, count)
                .subscribeOn(Schedulers.elastic())
                .map(i -> clientMessageService.sendMessage(payload))
                .collect(Collectors.joining(System.lineSeparator())));
    }

    @GetMapping("stream")
    public Mono<String> stream(@RequestParam(name = "payload", defaultValue = "very important payload") String payload,
                               @RequestParam(name = "count", defaultValue = "1") int count) {
        return Mono.just(clientMessageService.sendMessageToStream(payload, count));
    }
}