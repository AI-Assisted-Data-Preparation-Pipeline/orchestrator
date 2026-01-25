package orchestrator.domain.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "jobs")
@Getter
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobState state;

    @Column(nullable = false)
    private String uploadPath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    protected Job() {}

    public Job(String uploadPath) {
        this.state = JobState.FILE_UPLOADED;
        this.uploadPath = uploadPath;
        this.createdAt = LocalDateTime.now();
    }

    // TODO: 상태 전이 메서드
}
