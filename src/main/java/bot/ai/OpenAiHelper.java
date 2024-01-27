package bot.ai;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.moderation.Moderation;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import com.theokanning.openai.service.OpenAiService;
import bot.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class OpenAiHelper {
    // Application configuration from properties file
    private final Config config = Config.getInstance();

    // Log4j logging
    private static final Logger logger = LoggerFactory.getLogger(OpenAiHelper.class);

    // Singleton
    private static OpenAiHelper instance;

    // OpenAi-java service API
    private static OpenAiService service;

    public static OpenAiHelper getInstance() {
        return instance == null ? (instance = new OpenAiHelper()) : instance;
    }

    private OpenAiHelper() {
        service = new OpenAiService(config.getGptToken(), Duration.ofSeconds(Config.getInstance().getApiTimeout()));
    }

    public static void updateRole(String content, ServerConfig serverConfig) {
        ChatMessage systemMessage =  serverConfig.getChatMessages().get(0);
        if(systemMessage.getRole().equals(AiChatMessages.SYSTEM_ROLE)) {
            systemMessage.setContent(content);
        }
        serverConfig.getChatMessages().emptyChat();
    }

    public String makeOpenAiChatCompletionRequest(String request, ServerConfig serverConfig) {
        logger.debug("maxOpenAAiChatCompletionRequest({})", request);
        String response = config.getNonComplianceBotReply();
        logger.debug("got complience response: {}", response);

        if(requestMatchesContentPolicy(request)) {
            logger.debug("Create new chat message and add it to chat history");
            serverConfig.getChatMessages().add(new ChatMessage("user", request));

            logger.debug("creating openAi chat request");
            var chatRequest = ChatCompletionRequest.builder()
                    .messages(serverConfig.getChatMessages())
                    .maxTokens(serverConfig.getOpenAiMaxTokens())
                    .temperature(serverConfig.getOpenAiTemperature())
                    .model(serverConfig.getOpenAiModel())
                    .build();

            try {
                logger.debug("attempting to parse response");
                ChatCompletionResult completionResult = service.createChatCompletion(chatRequest);
                response = completionResult.getChoices().get(0).getMessage().getContent();
                serverConfig.getChatMessages().add(completionResult.getChoices().get(0).getMessage());
            } catch (Exception e) {
                logger.error("exception occur when making chatCompletion");
                response = config.getRequestFailureBotReply();
                e.printStackTrace();
            }


            if (response.isBlank() || response.isEmpty()) {
                logger.debug("respoonse was blank or empty");
                response = config.getRequestFailureBotReply();
            }
        }

        logger.debug("returning: {}", response);
        return response;
    }
    /**
     * OpenAi-java Completion request
     * @param request prompt for the AI
     * @return openAi response
     */
    public String makeOpenAiCompletionRequest(String request, ServerConfig serverConfig) {
        String response = config.getNonComplianceBotReply();

        if(requestMatchesContentPolicy(request)) {
            var completionRequest = CompletionRequest.builder()
                    .prompt(request)
                    .model(serverConfig.getOpenAiModel())
                    .echo(false)
                    .temperature(serverConfig.getOpenAiTemperature())
                    .maxTokens(serverConfig.getOpenAiMaxTokens())
                    .build();

                try {
                    CompletionResult completionResult = service.createCompletion(completionRequest);
                    response = completionResult.getChoices().get(0).getText()
                            .replaceFirst(request + "\n", "");
                } catch (Exception e) {
                    response = config.getRequestFailureBotReply();
                }


                if (response.isBlank() || response.isEmpty()) {
                    response = config.getRequestFailureBotReply();
                }
        }

        return response;
    }

    public String makeOpenAiImageRequest(String request) {
        String response = config.getNonComplianceBotReply();

        if(requestMatchesContentPolicy(request)) {
            CreateImageRequest imageRequest = CreateImageRequest.builder()
                    .prompt(request)
                    .size(config.getOpenAiImageSize())
                    .responseFormat(config.getOpenAiImageResponseFormat())
                    .build();

                try {
                    ImageResult imageResult = service.createImage(imageRequest);
                    response = imageResult.getData().get(0).getUrl();
                } catch(Exception e) {
                    e.printStackTrace();
                    response = config.getRequestFailureBotReply();
                }

                if(response.isBlank() || response.isEmpty()) {
                    response = config.getRequestFailureBotReply();
                }
        }

        return response;
    }

    public boolean requestMatchesContentPolicy(String request) {
        boolean requestOk = true;
        ModerationRequest moderationRequest = ModerationRequest.builder()
                .input(request)
                .model(OpenAiModels.TEXT_MODERATION_STABLE.getId())
                .build();
        ModerationResult result = service.createModeration(moderationRequest);
        for(Moderation moderation: result.getResults()) {
            if (moderation.flagged) {
                requestOk = false;
                break;
            }
        }

        return requestOk;
    }

    public static boolean isChatModel(String model) {
        return model.equals(OpenAiModels.GPT_3_5_TURBO.getId())
                || model.equals(OpenAiModels.GPT_4.getId())
                || model.equals(OpenAiModels.GPT_4_TURBO.getId())
                || model.equals(OpenAiModels.GPT_3_5_TURBO_1106.getId());
    }

    public static boolean isCompletionModel(String model) {
        return model.equals(OpenAiModels.DAVINCI_2.getId())
                || model.equals(OpenAiModels.BABBAGE_002.getId());
    }

    /**
     * All possible models that can be used in a request
     */
    public enum OpenAiModels {
        DAVINCI_2("davinci-002"),
        BABBAGE_002("babbage-002"),
        TEXT_EMBEDDING_3_LARGE("text-embedding-3-large"),
        TEXT_EMBEDDING_3_SMALL("text-embedding-3-small"),
        TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),
        TEXT_MODERATION_LATEST("text-moderation-latest"),
        TEXT_MODERATION_STABLE("text-moderation-stable"),
        TEXT_MODERATION_007("text-moderation-007"),
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
        GPT_4("gpt-4"),
        GPT_4_TURBO("gpt-4-turbo-preview"); // WARNING May Become Deprecated

        private final String id;

        OpenAiModels(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
