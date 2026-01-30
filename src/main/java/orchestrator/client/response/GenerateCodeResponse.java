package orchestrator.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateCodeResponse {

    private String code;
    private String explain;
}
