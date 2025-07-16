package com.demo.ailog.analogy.controller;

import com.demo.ailog.analogy.domain.BaseSearchDTO;
import com.demo.ailog.analogy.service.AnalogyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analogy")
@Slf4j
@RequiredArgsConstructor
public class AnalogyController {

    private final AnalogyService service;

    @GetMapping
    protected String response(BaseSearchDTO search) {
        return service.response(search);
    }

}
