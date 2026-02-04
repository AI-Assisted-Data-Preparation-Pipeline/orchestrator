package orchestrator.infra.client.ai_engine;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import orchestrator.common.exceptions.ExternalServerException;
import orchestrator.common.exceptions.InternalServerException;
import orchestrator.infra.client.ai_engine.request.GenerateCodeRequest;
import orchestrator.infra.client.ai_engine.response.GenerateCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class AiClient {

    private final WebClient webClient;
    private final Validator validator;

    public AiClient(
        @Value("${ai-engine.base-url}") String baseUrl,
        Validator validator
    ) {
        log.info("AiClient baseUrl: {}", baseUrl);

        this.validator = validator;

        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(30));

        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String generateCode(List<String> fileNames, String prompt) {
        GenerateCodeResponse response = call(
            webClient.post()
                .uri("/api/v1/generate")
                .bodyValue(new GenerateCodeRequest(fileNames, prompt))
                .retrieve()
                .bodyToMono(GenerateCodeResponse.class)
        );

        validateDto(response);
        return response.getCode();
    }

    private <T> T call(Mono<T> mono) {
        try {
            return mono.block();
        } catch (WebClientResponseException e) {
            log.error("AI engine responded with error. status={}, body={}",
                e.getStatusCode(),
                e.getResponseBodyAsString()
            );
            throw new ExternalServerException("ai-engine 에러 응답", e);
        } catch (WebClientRequestException e) {
            throw new ExternalServerException("ai-engine 요청 실패", e);
        }  catch (Exception e) {
            throw new InternalServerException("Unexpected AiClient error", e);
        }
    }

    private <T> void validateDto(T dto) {
        if (dto == null) {
            throw new ExternalServerException("Empty response body");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorDetail = violations.stream()
                .map(v -> String.format(
                    "field=%s, message=%s, rejectedValue=%s",
                    v.getPropertyPath(),
                    v.getMessage(),
                    v.getInvalidValue()
                ))
                .collect(Collectors.joining(" | "));
            log.error("AI engine payload validation failed: {}", errorDetail);
            throw new ExternalServerException("ai-engine 응답 이상. 검증 실패");
        }
    }
}
