package orchestrator.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmitPromptRequest {

    private String prompt;
}
