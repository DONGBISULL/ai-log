package com.demo.ailog.embed.controller;

import com.demo.ailog.embed.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/embed")
@RequiredArgsConstructor
public class EmbedController {

    private final EmbeddingService service;

//    @GetMapping("/test")
//    protected void test() {
//        service.test();
//    }
}
