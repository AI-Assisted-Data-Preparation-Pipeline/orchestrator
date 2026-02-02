package orchestrator.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import orchestrator.exceptions.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkerService {

    private final String volumePath;
    private final String dockerImage;
    private final FileStorageService fileStorageService;
    private final JobService jobService;

    public WorkerService(
        @Value("${file.volume-path}") String volumePath,
        @Value("${docker.image}") String dockerImage,
        FileStorageService fileStorageService,
        JobService jobService
    ) {
        this.volumePath = volumePath;
        this.dockerImage = dockerImage;
        this.fileStorageService = fileStorageService;
        this.jobService = jobService;
        log.info("WorkerService running. volume-path: {}, docker-image: {}", volumePath, dockerImage);
    }

    public void runWorkerContainer(UUID jobId) {
        String jobDir = volumePath + "/" + jobId;

        List<String> command = List.of(
            "docker", "run",
            "--rm",
            "--name", "worker-" + jobId,
            "-v", jobDir + ":/workspace",
            dockerImage,
            "python", "/workspace/main.py"
        );

        log.info("Running worker container. jobId={}, command={}", jobId, command);
        jobService.startExecution(jobId);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            log.info("Docker process started. pid={}", process.pid());

            try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                List<String> buffer = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    log.info("[worker-{}] {}", jobId, line);
                    buffer.add(line);
                    if (buffer.size() >= 10) {
                        jobService.addExecutionLogs(jobId, buffer);
                        buffer.clear();
                    }
                }
                if (!buffer.isEmpty()) {
                    jobService.addExecutionLogs(jobId, buffer);
                }
            }

            int exitCode = process.waitFor();
            log.info("Worker container exited. jobId={}, exitCode={}", jobId, exitCode);

            if (exitCode != 0) {
                log.error("Worker container failed. jobId={}", jobId);
                jobService.executionFailed(jobId);
                return;
            }

            String filePath = fileStorageService.getOutputFilePath(jobId.toString());
            jobService.executionSuccess(jobId, filePath);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerException("Failed to execute worker container", e);
        }
    }
}
