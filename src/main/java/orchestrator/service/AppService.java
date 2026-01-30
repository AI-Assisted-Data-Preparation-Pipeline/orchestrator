package orchestrator.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.client.AiClient;
import orchestrator.domain.job.Job;
import orchestrator.dto.response.SubmitPromptResponse;
import orchestrator.dto.response.UploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AppService {

    private final FileStorageService fileStorageService;
    private final JobService jobService;
    private final AiClient aiClient;

    public UploadResponse uploadFile(MultipartFile file) {
        Job created = jobService.createJob();
        fileStorageService.storeFile(file, created.getId().toString());
        String fileName = file.getOriginalFilename();
        jobService.fileUploaded(created.getId(), fileName);
        return new UploadResponse(created.getId(), created.getState(), "file uploaded successfully");
    }

    public SubmitPromptResponse generateCode(UUID jobId, String prompt) {
        List<String> fileNames = List.of(jobService.getFileName(jobId));
        String generatedCode = aiClient.generateCode(fileNames, prompt);

        fileStorageService.makePyFile(jobId.toString(), generatedCode);

        jobService.setGeneratedCode(jobId, generatedCode);
        return new SubmitPromptResponse(jobId, generatedCode);
    }

}
