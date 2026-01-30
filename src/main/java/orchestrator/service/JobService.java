package orchestrator.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.domain.job.Job;
import orchestrator.exceptions.BadRequestException;
import orchestrator.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob() {
        Job instance = Job.instance();
        return jobRepository.save(instance);
    }

    public void fileUploaded(UUID jobId, String fileName) {
        Job found = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        found.fileUploaded(fileName);
    }

    public String getFileName(UUID jobId) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        return found.getFileName();
    }

    public void setGeneratedCode(UUID jobId, String generatedCode) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        found.setGeneratedCode(generatedCode);
    }
}
