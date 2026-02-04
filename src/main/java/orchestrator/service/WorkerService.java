package orchestrator.service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import orchestrator.common.exceptions.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Async("dockerWorkerExecutor")
    public void runWorkerContainer(UUID jobId) {
        String jobDir = volumePath + "/" + jobId;

        List<String> command = List.of(
            "docker", "run",
            "--rm",
            "--name", "worker-" + jobId,
            "-v", jobDir + ":/workspace",
            "-w", "/workspace",
            dockerImage,
            "python", "main.py"
        );

        jobService.startExecution(jobId);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        log.info("Running worker container. jobId={}, command={}", jobId, command);
        boolean success = false;
        Process process = null;

        try {
            process = pb.start();
            log.debug("[WORKER_START] jobId={}, pid={}", jobId, process.pid());

            // worker 실행 로그 처리
            try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                List<String> buffer = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    log.debug("[WORKER_LOG][worker-{}] {}", jobId, line);
                    buffer.add(line);
                    if (buffer.size() >= 10) {
                        jobService.addExecutionLogs(jobId, buffer);
                        buffer.clear();
                    }
                }
                if (!buffer.isEmpty()) {
                    jobService.addExecutionLogs(jobId, buffer);
                }
            } catch (IOException e) {
                // 여기서 발생한 IOException은 로그 수집 실패 > job 실패가 아님
                log.error("[WORKER_LOG][worker-{}] 로그 수집 실패", jobId);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("[WORKER_FAIL] jobId={}, exitCode={}", jobId, exitCode);
                return;
            }

            log.debug("[WORKER_DONE] jobId={}, exitCode={}", jobId, exitCode);
            String filePath = fileStorageService.getOutputFilePath(jobId.toString());
            jobService.executionSuccess(jobId, filePath);
            success = true;

        } catch (IOException e) {
            throw new InternalServerException("Run Worker Container Failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerException("인터럽트 발생. run worker container 종료", e);
        } finally {
            if (!success) {
                jobService.executionFailed(jobId);
            }
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    @PostConstruct
    public void checkDockerAvailability() {
        try {
            Process process = new ProcessBuilder("docker", "ps")
                .redirectErrorStream(true)
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Docker is accessible.");
            } else {
                log.error("Docker is installed but daemon access failed. exitCode={}", exitCode);
            }

        } catch (IOException e) {
            log.error("Docker CLI not found. WorkerService may not function.", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Docker availability check interrupted", e);
        }
    }
}
