package orchestrator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import orchestrator.domain.job.Job;

@Getter
@AllArgsConstructor
public class JobResponse {

    private final String id;
    private final String state;
    private final String path;

    public static JobResponse from(Job o) {
        return new JobResponse(o.getId().toString(), o.getState().toString(), o.getUploadPath());
    }
}
