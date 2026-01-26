package orchestrator.service;

import lombok.RequiredArgsConstructor;
import orchestrator.domain.job.Job;
import orchestrator.dto.response.UploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AppService {

    private final FileStorageService fileStorageService;
    private final JobService jobService;

    public UploadResponse uploadFile(MultipartFile file) {
        Job created = jobService.createJob();
        String path = fileStorageService.storeFile(file, created.getId().toString());
        jobService.fileUploaded(created.getId(), path);
        return new UploadResponse(created.getId(), created.getState(), "file uploaded successfully");
    }

}
