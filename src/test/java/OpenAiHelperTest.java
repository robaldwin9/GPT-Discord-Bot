import org.junit.jupiter.api.BeforeEach;

import static reactor.core.publisher.Mono.when;

public class OpenAiHelperTest {

    OpenAiHelper aihelper;

    AiChatMessages messages;

    @BeforeEach
    public void setup() {
        Config config = Config.getInstance();
        aihelper = OpenAiHelper.getInstance();
        messages = new AiChatMessages(config.getOpenAiMaxTokens(), config.getBotPersonality());
    }
}
