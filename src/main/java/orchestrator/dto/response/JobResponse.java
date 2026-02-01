package orchestrator.dto.response;

import java.net.URI;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import orchestrator.domain.job.Job;

@Getter
@AllArgsConstructor
public class JobResponse {

    private final String id;
    private final String state;
    private final String log;
    private final URI outputUrl;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public static JobResponse from(Job job) {
        return new JobResponse(
            job.getId().toString(),
            job.getState().toString(),
            job.getLog(),
            URI.create("/api/jobs/" + job.getId() + "/output"),
            job.getStartedAt(),
            job.getFinishedAt()
        );
    }
}
