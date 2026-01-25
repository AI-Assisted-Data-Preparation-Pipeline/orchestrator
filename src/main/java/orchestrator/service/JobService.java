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

    public Job createJob(String uploadPath) {
        Job job = new Job(uploadPath);
        return jobRepository.save(job);
    }

    private Job getJob(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }
}
