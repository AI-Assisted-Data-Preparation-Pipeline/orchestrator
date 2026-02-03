package orchestrator.common.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmitPromptResponse {

    private final UUID jobId;
    private final String generatedCode;
}
