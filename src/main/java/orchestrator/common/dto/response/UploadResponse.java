package orchestrator.common.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import orchestrator.domain.job.JobState;

@Getter
@AllArgsConstructor
public class UploadResponse {

    private final UUID jobId;
    private final JobState jobState;
    private final String message;
}
