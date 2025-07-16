package com.demo.ailog.analogy.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatClientOptions {
    private String model;
    private String apiKey;
    private String baseUrl;
}
