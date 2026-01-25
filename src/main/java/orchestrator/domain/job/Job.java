package orchestrator.domain.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "jobs")
@Getter
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobState state;

    private String uploadPath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    protected Job() {}

    public static Job instance() {
        return new Job(null, JobState.CREATED, null, LocalDateTime.now(), null, null);
    }

    public void fileUploaded(String path) {
        this.uploadPath = path;
        this.state = JobState.FILE_UPLOADED;
    }
}
