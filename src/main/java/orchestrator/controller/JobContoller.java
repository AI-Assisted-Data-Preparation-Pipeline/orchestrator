package orchestrator.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import orchestrator.domain.job.Job;
import orchestrator.dto.response.JobResponse;
import orchestrator.service.FileStorageService;
import orchestrator.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobContoller {

    private final FileStorageService fileStorageService;
    private final JobService jobService;

    @PostMapping("/upload")
    public ResponseEntity<JobResponse> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        Job created = jobService.createJob();
        String filePath = fileStorageService.storeFile(file, created.getId().toString());
        created.fileUploaded(filePath);
        String location = "/api/v1/jobs/" + created.getId().toString();
        return ResponseEntity.created(URI.create(location)).body(
            JobResponse.from(created)
        );
    }
}
