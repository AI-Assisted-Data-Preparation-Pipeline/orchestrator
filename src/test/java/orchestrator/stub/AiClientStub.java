package orchestrator.stub;

import java.util.List;
import orchestrator.infra.client.ai_engine.AiClient;

public class AiClientStub extends AiClient {

    public AiClientStub() {
        super("http://stub", null);
        // validator: emptyValidator or AiClient 인터페이스로 분리 필요
    }

    @Override
    public String generateCode(List<String> fileNames, String prompt) {
        return "test-generated-code";
    }
}

