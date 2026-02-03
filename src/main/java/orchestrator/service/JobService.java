package orchestrator.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import orchestrator.common.exceptions.BadRequestException;
import orchestrator.common.exceptions.InternalServerException;
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

    public void fileUploaded(UUID jobId, String fileName) {
        Job found = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        found.fileUploaded(fileName);
    }

    @Transactional(readOnly = true)
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

    protected void startExecution(UUID jobId) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new InternalServerException("Tried to access Non-Exist Job: " + jobId));
        found.startExecute();
    }

    protected void addExecutionLogs(UUID jobId, List<String> buffer) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new InternalServerException("Tried to access Non-Exist Job: " + jobId));
        buffer.forEach(found::addExecutionLog);
    }

    protected void executionFailed(UUID jobId) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new InternalServerException("Tried to access Non-Exist Job: " + jobId));
        found.executeFailed();
    }

    protected void executionSuccess(UUID jobId, String outputPath) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new InternalServerException("Tried to access Non-Exist Job: " + jobId));
        found.executeSuccess(outputPath);
    }

    protected Job getJob(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
    }

    protected String getOutputPath(UUID jobId) {
        Job found = jobRepository.findById(jobId)
            .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
        return found.getOutputPath();
    }
}
