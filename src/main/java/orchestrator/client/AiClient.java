package orchestrator.client;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import orchestrator.client.request.GenerateCodeRequest;
import orchestrator.client.response.GenerateCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class AiClient {

    private final WebClient webClient;

    public AiClient(@Value("${ai-engine.base-url}") String baseUrl) {
        log.info("AiClient baseUrl: {}", baseUrl);

        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(30));

        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String generateCode(List<String> fileNames, String prompt) {
        GenerateCodeResponse response = webClient.post()
            .uri("/api/v1/generate")
            .bodyValue(new GenerateCodeRequest(fileNames, prompt))
            .retrieve()
            .bodyToMono(GenerateCodeResponse.class)
            .block();
        return response.getGeneratedCode();
    }
}
