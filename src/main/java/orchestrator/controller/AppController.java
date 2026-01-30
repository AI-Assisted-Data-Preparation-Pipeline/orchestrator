package orchestrator.controller;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.dto.request.SubmitPromptRequest;
import orchestrator.dto.response.SubmitPromptResponse;
import orchestrator.dto.response.UploadResponse;
import orchestrator.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        UploadResponse response = appService.uploadFile(file);
        String location = "/api/jobs/" + response.getJobId();
        return ResponseEntity.created(URI.create(location)).body(response);
    }

    @PostMapping("/submit-prompt")
    public ResponseEntity<SubmitPromptResponse> submitPrompt(@RequestBody SubmitPromptRequest request) {
        UUID jobId = UUID.fromString(request.getJobId());
        SubmitPromptResponse response = appService.generateCode(jobId, request.getPrompt());
        return ResponseEntity.ok(response);
    }
}
