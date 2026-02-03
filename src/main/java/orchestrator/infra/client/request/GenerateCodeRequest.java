package orchestrator.infra.client.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateCodeRequest {

    private List<String> file_names;
    private String user_input;
}
