package orchestrator.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import orchestrator.exceptions.CriticalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final String baseUploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String baseUploadDir) {
        this.baseUploadDir = baseUploadDir;

        Path baseDir = Paths.get(baseUploadDir);
        try {
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }
            if (!Files.isDirectory(baseDir) || !Files.isWritable(baseDir)) {
                throw new IllegalStateException("Upload base dir is invalid");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String jobId) {
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            // jobId 별 디렉토리 생성
            Path jobDir = Paths.get(baseUploadDir, jobId);
            if (!Files.exists(jobDir)) {
                Files.createDirectories(jobDir);
            }

            // 파일 저장
            Path targetLocation = jobDir.resolve(fileName);
            file.transferTo(targetLocation.toFile());

            return targetLocation.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new CriticalException("Failed to store file", e);
        }
    }
}
