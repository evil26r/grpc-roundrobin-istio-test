package com.grpc.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final ClientMessageService clientMessageService;

    @GetMapping("rpc")
    public Mono<String> rpc(@RequestParam(name = "payload", defaultValue = "very important payload") String payload) {
        return Mono.just(clientMessageService.sendMessage(payload));
    }

    @GetMapping("stream")
    public Mono<String> stream(@RequestParam(name = "payload", defaultValue = "very important payload") String payload,
                               @RequestParam(name = "count", defaultValue = "1") int count) {
        return Mono.just(clientMessageService.sendMessageToStream(payload, count));
    }
}
