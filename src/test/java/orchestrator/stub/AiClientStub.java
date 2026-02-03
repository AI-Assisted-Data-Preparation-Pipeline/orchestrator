package orchestrator.stub;

import java.util.List;
import orchestrator.infra.client.AiClient;

public class AiClientStub extends AiClient {

    public AiClientStub() {
        super("http://stub"); // 생성자 맞추기용
    }

    @Override
    public String generateCode(List<String> fileNames, String prompt) {
        return "test-generated-code";
    }
}

