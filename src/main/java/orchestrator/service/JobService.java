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
        getOrThrowBadRequest(jobId)
            .fileUploaded(fileName);
    }

    public void setGeneratedCode(UUID jobId, String generatedCode) {
        getOrThrowBadRequest(jobId)
            .setGeneratedCode(generatedCode);
    }

    public void startExecution(UUID jobId) {
        getOrThrowInternal(jobId)
            .startExecute();
    }

    public void addExecutionLogs(UUID jobId, List<String> buffer) {
        Job found = getOrThrowInternal(jobId);
        buffer.forEach(found::addExecutionLog);
    }

    public void executionFailed(UUID jobId) {
        getOrThrowInternal(jobId)
            .executeFailed();
    }

    public void executionSuccess(UUID jobId, String outputPath) {
        getOrThrowInternal(jobId)
            .executeSuccess(outputPath);
    }

    @Transactional(readOnly = true)
    protected String getValidOutputPath(UUID jobId) {
        Job job = getOrThrowBadRequest(jobId);
        if (!job.isFinished()) {
            throw new BadRequestException("실행이 아직 종료되지 않았습니다.");
        }
        if (!job.isSucceeded()) {
            throw new BadRequestException("실행에 실패해 output이 생성되지 않았습니다.");
        }
        return job.getOutputPath();
    }

    @Transactional(readOnly = true)
    public String getInputFileName(UUID jobId) {
        return getOrThrowBadRequest(jobId)
            .getFileName();
    }

    @Transactional(readOnly = true)
    public Job getOrThrowBadRequest(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new BadRequestException("job not found: " + jobId));
    }

    private Job getOrThrowInternal(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new InternalServerException("Tried to access Non-Exist job: " + jobId));
    }
}
