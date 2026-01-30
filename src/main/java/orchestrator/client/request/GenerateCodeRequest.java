package orchestrator.client.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateCodeRequest {

    private List<String> fileNames;
    private String prompt;
}
