import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.moderation.Moderation;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenAiHelper {
    // Application configuration from properties file
    private final Config config = Config.getInstance();

    // Log4j logging
    private static final Logger logger = LogManager.getLogger(OpenAiHelper.class);

    // Singleton
    private static OpenAiHelper instance;

    // OpenAi-java service API
    private static OpenAiService service;

    public static OpenAiHelper getInstance() {
        return instance == null ? (instance = new OpenAiHelper()) : instance;
    }

    private static final String POLICY_VIOLATION_TEXT = "Sorry request does not comply with OpenAI's Content Policy";

    private OpenAiHelper() {
        service = new OpenAiService(config.getGptToken());
    }

    /**
     * OpenAi-java Completion request
     * @param request prompt for the AI
     * @return openAi response
     */
    public String makeOpenAiCompletionRequest(String request) {
        String response = POLICY_VIOLATION_TEXT;

        if(requestMatchesContentPolicy(request)) {
            var completionRequest = CompletionRequest.builder()
                    .prompt(request)
                    .model(config.getOpenAiModel())
                    .echo(true)
                    .temperature(config.getOpenAiTemperature())
                    .maxTokens(config.getOpenAiMaxTokens())
                    .build();

            CompletionResult completionResult = service.createCompletion(completionRequest);
            response = completionResult.getChoices().get(0).getText().replaceFirst(request + "\n", "");
        }

        return response;
    }

    public String makeOpenAiImageRequest(String request) {
        String result = POLICY_VIOLATION_TEXT;

        if(requestMatchesContentPolicy(request)) {
            CreateImageRequest imageRequest = CreateImageRequest.builder()
                    .prompt(request)
                    .size("1024x1024")
                    .responseFormat("url")
                    .build();
            ImageResult imageResult = service.createImage(imageRequest);
            result = imageResult.getData().get(0).getUrl();
        }

        return result;
    }

    public boolean requestMatchesContentPolicy(String request) {
        boolean requestOk = true;
        ModerationRequest moderationRequest = com.theokanning.openai.moderation.ModerationRequest.builder()
                .input(request)
                .build();

        ModerationResult result = service.createModeration(moderationRequest);
        for(Moderation moderation: result.getResults()) {
            if (moderation.flagged) {
                logger.debug("moderation check flagged: {}", moderation.toString());
                requestOk = false;
                break;
            }
        }

        return requestOk;
    }

    /**
     * All possible models that can be used in a request
     */
    public enum OpenAiModels {
        DAVINCI_3("text-davinci-003"),
        CURIE_1("text-curie-001"),
        BABBAGE_1("text-babbage-001"),
        ADA_1("text-ada-001"),
        CODE_DAVINCI("conde-davinci-002"),
        CODE_CUSHMAN("code-chsman-001");

        private final String id;

        OpenAiModels(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
