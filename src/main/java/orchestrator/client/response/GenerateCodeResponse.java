package orchestrator.client.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateCodeResponse {

    @NotBlank
    private String code;
    private String explain;
}
