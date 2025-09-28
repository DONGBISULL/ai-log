package com.demo.ailog.common.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class AiClientsConfig {

    @Value("${ai.openai-key}")
    String openAiKey;

    @Bean
    @Qualifier("openaiChatModel")
    public ChatModel openaiChatModel(
            ToolCallingManager toolCallingManager,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ResponseErrorHandler responseErrorHandler
    ) {
        OpenAiApi openAiApi = new OpenAiApi(
                "https://api.openai.com/v1",
                () -> openAiKey,
                new LinkedMultiValueMap<>(),
                "/chat/completions",
                "/embeddings",
                restClientBuilder,
                webClientBuilder,
                responseErrorHandler
        );

        OpenAiChatOptions options = new OpenAiChatOptions();
        options.setModel("gpt-4o");

        return new OpenAiChatModel(
                openAiApi,
                options,
                toolCallingManager,
                retryTemplate,
                observationRegistry
        );
    }

    @Bean
    @Qualifier("openaiChatClient")
    public ChatClient openaiChatClient(@Qualifier("openaiChatModel") ChatModel model) {
        return ChatClient.create(model);
    }

}
