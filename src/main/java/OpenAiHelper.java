import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
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

    private OpenAiHelper() {
        service = new OpenAiService(config.getGptToken());
    }

    /**
     * OpenAi-java Completion request
     * @param request prompt for the AI
     * @return openAi response
     */
    public String makeOpenAiCompletionRequest(String request) {
        String response = "";
        var completionRequest = CompletionRequest.builder()
                .prompt(request)
                .model(config.getOpenAiModel())
                .echo(true)
                .temperature(config.getOpenAiTemperature())
                .maxTokens(config.getOpenAiMaxTokens())
                .build();

        CompletionResult result = service.createCompletion(completionRequest);
        response = result.getChoices().get(0).getText().replaceFirst(request + "\n", "");
        return response;
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
