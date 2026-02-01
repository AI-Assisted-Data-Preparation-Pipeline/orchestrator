package orchestrator.controller;

import java.io.File;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.dto.request.SubmitPromptRequest;
import orchestrator.dto.response.JobResponse;
import orchestrator.dto.response.SubmitPromptResponse;
import orchestrator.dto.response.UploadResponse;
import orchestrator.service.AppService;
import orchestrator.service.WorkerService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final WorkerService workerService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        UploadResponse response = appService.uploadFile(file);
        String location = "/api/jobs/" + response.getJobId();
        return ResponseEntity.created(URI.create(location)).body(response);
    }

    @PostMapping("/{jobId}/submit-prompt")
    public ResponseEntity<SubmitPromptResponse> submitPrompt(
        @PathVariable String jobId,
        @RequestBody SubmitPromptRequest request
    ) {
        SubmitPromptResponse response = appService.generateCode(UUID.fromString(jobId), request.getPrompt());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}/execute")
    public ResponseEntity<Void> executeJob(@PathVariable String jobId) {
        workerService.runWorkerContainer(UUID.fromString(jobId));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> viewJobDetail(@PathVariable String jobId) {
        return ResponseEntity.ok(appService.getJobResponse(UUID.fromString(jobId)));
    }

    @GetMapping("/{jobId}/output")
    public ResponseEntity<Resource> downloadOutput(@PathVariable String jobId) {
        File outputFile = appService.getOutputFile(UUID.fromString(jobId));
        Resource resource = new FileSystemResource(outputFile);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + outputFile.getName() + "\"")
            .contentLength(outputFile.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }
}
