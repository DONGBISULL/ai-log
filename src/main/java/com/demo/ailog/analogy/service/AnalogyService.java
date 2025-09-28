package com.demo.ailog.analogy.service;

import com.demo.ailog.analogy.domain.BaseSearchDTO;
import com.demo.ailog.analogy.domain.ErrorAnalysisReportDTO;
import com.demo.ailog.common.exception.LLMResponseException;
import com.demo.ailog.common.exception.ParsingException;
import com.demo.ailog.embed.service.EmbeddingService;
import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.processor.consumer.parser.LogHashGenerator;
import com.demo.ailog.processor.consumer.parser.LogParserManager;
import com.demo.ailog.processor.consumer.service.ErrorAnalysisService;
import com.demo.ailog.processor.consumer.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalogyService {

    @Value("classpath:prompts/log_analysis_prompt_ko.txt")
    private Resource resourceFile;

    private final ChatClient chatClient;

    private final LogParserManager parserManager;

    private final ErrorAnalysisService errorAnalysisService;

    private final EmbeddingService embeddingService;

    private final LogService service;

    private final ObjectMapper mapper;

    public AnalogyService(@Qualifier("openaiChatClient") ChatClient chatClient, LogParserManager parserManager, ErrorAnalysisService errorAnalysisService, EmbeddingService embeddingService, LogService service, ObjectMapper mapper) {
        this.chatClient = chatClient;
        this.parserManager = parserManager;
        this.errorAnalysisService = errorAnalysisService;
        this.embeddingService = embeddingService;
        this.service = service;
        this.mapper = mapper;
    }

    @Transactional
    public void analysisErrors(RawLogEntity entity) throws NoSuchAlgorithmException {
        if (errorAnalysisService.existsByRawId(entity.getId())) {
            log.info("Already analyzed rawLogId: {}", entity.getId());
            return;
        }
        Document document = embeddingService.getDocument(entity.getMessage());
        ErrorAnalysisDTO newLog = null;
        if (document != null) {
            String rawLogId = document.getMetadata().get("rawLogId").toString();
            ErrorAnalysisDTO target = errorAnalysisService.findByRawId(Long.valueOf(rawLogId));
            newLog = target.toBuilder()
                    .id(null)
                    .rawLogId(Long.valueOf(entity.getId()))
                    .build();
        } else {
            newLog = summarize(entity.getMessage());
            newLog.setRawLogId(entity.getId());
            log.info(" generateLogHash {} ", LogHashGenerator.generateLogHash(newLog.getErrorType() + " " + newLog.getNormalizedPattern()));
            newLog.setPatternHash(LogHashGenerator.generateLogHash(newLog.getErrorType() + " " + newLog.getNormalizedPattern()));
            embeddingService.addVectorLog(entity.getMessage(), newLog);
        }
        errorAnalysisService.add(newLog);
        service.updateProcessed(entity.getId());
    }

    /**
     * 실제 발생한 에러 로그 요약
     */
    public ErrorAnalysisDTO summarize(String rawLog) {
        log.info("======= summarize start =======");
        String prompt = """
                다음 에러 로그를 분석해주세요. 
                **단, `error_summary`와 `fix_approach` 값은 반드시 한글로 작성**하며 나머지 필드는 원본 기술 표기(영문) 그대로 사용하세요.
                분석 시 객관적 사실만 응답하세요.
                
                에러 로그:
                ```
                %s
                ```
                
                다음 JSON 형식으로 **로그에서 확인할 수 있는 사실만** 응답하세요. 
                설명 없이, 코드 블록 없이, 문자열만 반환하세요.
                
                **주의**: 반드시 아래 JSON 구조와 정확히 일치하며, Markdown 코드 블록 없이 순수 JSON만 반환해야 합니다.
                
                [예상 JSON 구조 예시]
                {
                  "error_category": "APPLICATION_ERROR",
                  "error_type": "ARRAY_INDEX_OUT_OF_BOUNDS",
                  "error_summary": "한글 요약",
                  "normalized_pattern": "정규화된_패턴",
                  "root_cause": "근본_원인(영문 가능)",
                  "technical_details": {
                       "primary_identifier": "주요_식별자",
                       "location_info": "위치_정보",
                       "context_info": "컨텍스트_정보",
                       "system_layer": "시스템_레이어"
                  },
                  "affected_component": "영향받는_기술컴포넌트",
                  "fix_approach": "한글 수정 방향"
                }
                
                **분류 기준:**
                - error_category: APPLICATION_ERROR, DATABASE_ERROR, NETWORK_ERROR, CONFIGURATION_ERROR, SYSTEM_ERROR
                - component_layer: CONTROLLER, SERVICE, REPOSITORY, DATABASE, EXTERNAL_API, UNKNOWN
                - affected_component: 로그에서 확인 가능한 기술적 컴포넌트명만 (클래스명, 모듈명 등)
                - fix_approach: 기술적 수정 방향만 (구체적 해결책 금지, 반드시 한글)
                
                **주의사항:**
                1. 로그에 없는 정보는 "UNKNOWN" 또는 빈 문자열로 표시
                2. 비즈니스 영향도나 우선순위 판단 금지
                3. 특정 애플리케이션 구조 가정 금지
                4. `error_summary`와 `fix_approach`는 반드시 한글로 작성
                """.formatted(rawLog);
        try {
            String content = chatClient.prompt(prompt + rawLog).call().content();
            log.info("AnalogyService :: summarize {} ", content);
            ErrorAnalysisDTO parsed = parserManager.parse(content);
            log.info("Successfully received response from Ollama {} ", parsed);
            return parsed;
        } catch (Exception e) {
            log.error("AI 요약 처리 중 오류 발생 {} ", e.getMessage());
            throw new RuntimeException("AI 요약 처리 실패", e);
        }
    }

    /**
     * 기간 동안의 에러 로그를 그룹화하여 에러 로그 추출 및 분석 반환
     */
    public ErrorAnalysisReportDTO response(BaseSearchDTO search) {
        try {
            log.info("Starting log analysis for period: {} to {}", search.getStartTime(), search.getEndTime());

            String content = new String(resourceFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            StringBuilder msg = new StringBuilder();

            /* 추후에는 서비스 ID (구분 값) 받아서  */
            List<RawLogEntity> list = service.findByTimestampBetween(search.getStartTime(), search.getEndTime());

            List<Long> targetIds = list.stream().map(RawLogEntity::getId).toList();

            List<ErrorAnalysisDTO> targets = errorAnalysisService.listInRawId(targetIds);

            Map<String, List<ErrorAnalysisDTO>> patternedItems = targets
                    .stream()
                    .collect(Collectors.groupingBy(ErrorAnalysisDTO::getPatternHash));

            List<ErrorAnalysisDTO> analogyTargets = patternedItems.values()
                    .stream().map(item -> {
                                ErrorAnalysisDTO analysis = item.stream().max(
                                                Comparator.comparing(ErrorAnalysisDTO::getCreatedAt)
                                        )
                                        .orElse(null);
                                if (analysis != null) {
                                    analysis.setErrorCount(item.stream().count());
                                }
                                return analysis;
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            for (ErrorAnalysisDTO errorInfo : analogyTargets) {
                msg.append("--- LOG START ---\n");
                msg.append(String.format("[%s] %s | Count: %d | %s | Fix: %s\n",
                        errorInfo.getErrorType(),
                        errorInfo.getAffectedComponent(),
                        errorInfo.getErrorCount(),
                        errorInfo.getErrorSummary(),
                        errorInfo.getFixApproach()
                ));
                msg.append("--- LOG END ---\n");
            }

            content = content.replace("{start_time}", String.valueOf(search.getStartTime()));
            content = content.replace("{end_time}", String.valueOf(search.getEndTime()));
            return callLLMAndParseResponse(content, msg.toString());
        } catch (IOException e) {
            log.error("Failed to read prompt file: {}", e.getMessage());
            throw new RuntimeException("프롬프트 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private ErrorAnalysisReportDTO callLLMAndParseResponse(String promptContent, String message) {
        String cleaned;
        try {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
                    chatClient.prompt()
                            .system(promptContent)
                            .user(message)
                            .call()
                            .content()
            );

            String result = future.get(60 * 3, TimeUnit.SECONDS);

            cleaned = result
                    .trim()
                    .replaceAll("^```json\\s*", "")  // 시작 부분의 ```json 제거
                    .replaceAll("```$", "")          // 끝 부분의 ``` 제거
                    .replaceAll("^`|`$", "")         // 단일 백틱 제거
                    .trim();
            log.info("Successfully received response from LLM: {}", cleaned);

        } catch (TimeoutException e) {
            log.error("LLM request timed out after 30 seconds", e);
            throw new LLMResponseException("AI 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("LLM request was interrupted", e);
            throw new LLMResponseException("AI 요청이 중단되었습니다.", e);
        } catch (Exception e) {
            log.error("Unexpected error during LLM request", e);
            throw new LLMResponseException("AI 요청 중 예상치 못한 오류가 발생했습니다.", e);
        }

        try {
            return mapper.readValue(cleaned, ErrorAnalysisReportDTO.class);
        } catch (IOException parseEx) {
            log.error("Failed to parse LLM response", parseEx);
            throw new ParsingException("LLM 응답 JSON 파싱 실패", parseEx);
        }
    }

}

