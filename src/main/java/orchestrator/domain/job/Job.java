package orchestrator.domain.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import orchestrator.exceptions.InternalServerException;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "jobs")
@Getter
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobState state;

    private String fileName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String generatedCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    protected Job() {}

    public static Job instance() {
        return new Job(null, JobState.CREATED, null, null, LocalDateTime.now(), null, null);
    }

    public void fileUploaded(String fileName) {
        assertState(JobState.CREATED);
        this.fileName = fileName;
        this.state = JobState.FILE_UPLOADED;
    }

    public void setGeneratedCode(String code) {
        assertState(JobState.FILE_UPLOADED);
        this.generatedCode = code;
        this.state = JobState.CODE_GENERATED;
    }

    private void assertState(JobState expected) {
        if (this.state != expected) {
            throw new InternalServerException("비정상 상태전환. expected=" + expected + ", actual=" + state);
        }
    }
}
