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

    public void fileUploaded(UUID jobId, String uploadPath) {
        Job found = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        found.fileUploaded(uploadPath);
    }
}
