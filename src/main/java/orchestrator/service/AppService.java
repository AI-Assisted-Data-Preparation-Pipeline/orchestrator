package orchestrator.service;

import java.io.File;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.common.dto.response.JobResponse;
import orchestrator.common.dto.response.SubmitPromptResponse;
import orchestrator.common.dto.response.UploadResponse;
import orchestrator.common.exceptions.InternalServerException;
import orchestrator.domain.job.Job;
import orchestrator.infra.client.ai_engine.AiClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
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
        List<String> fileNames = List.of(jobService.getInputFileName(jobId));
        String generatedCode = aiClient.generateCode(fileNames, prompt);
        fileStorageService.makePyFile(jobId.toString(), generatedCode);

        jobService.setGeneratedCode(jobId, generatedCode);
        return new SubmitPromptResponse(jobId, generatedCode);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobResponse(UUID jobId) {
        return JobResponse.from(jobService.getOrThrowBadRequest(jobId));
    }

    public File getOutputFile(UUID jobId) {
        String outputPath = jobService.getValidOutputPath(jobId);
        File file = fileStorageService.getFile(outputPath);

        if (file == null) {
            throw new InternalServerException("output 파일이 존재하지 않습니다. path: " + outputPath);
        }

        return file;
    }
}
