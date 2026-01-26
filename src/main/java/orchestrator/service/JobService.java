package orchestrator.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.domain.job.Job;
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

    public Job fileUploaded(UUID jobId, String uploadPath) {
        Job found = getJob(jobId);
        found.fileUploaded(uploadPath);
        return found;
    }

    private Job getJob(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }
}
