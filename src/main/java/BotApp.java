import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumSet;

public class BotApp {

    private static final Logger logger = LogManager.getLogger(BotApp.class);
    private static Config config;
    private static OpenAiHelper aiHelper;

    public static void main(String[] args) {

        // Load application configuration
         config = Config.getInstance();
         aiHelper = OpenAiHelper.getInstance();

        // Connect to discord
        var client = DiscordClient.create(config.getBotToken());
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) ->  {

            // Login event
            Mono<Void> onLogin = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                        final User self = event.getSelf();
                        logger.info("Logged in as {}:{}", self.getUsername(), self.getDiscriminator());
                    })).then();

            // User sends message to channel
            Mono<Void> onMessage = gateway.on(MessageCreateEvent.class, event -> {

                // check message for command, and respond
                Message message = event.getMessage();
                logger.debug("Discord message content: {}", message.getContent());

                if (message.getContent().charAt(0) == config.getCommandCharacter()
                        || message.getContent().charAt(0) == '/') {
                    String query = parseQuery(message.getContent());
                    switch (parseCommand(message.getContent())) {
                        case "gpt":
                            String response = aiHelper.makeOpenAiCompletionRequest(parseQuery(query));
                            if (response.isEmpty() || response.isBlank()) {
                               response = "A valid response was not generated by OpenAI, " +
                                       "consider trying again or providing more details";
                            }
                            String finalResponse = response;
                            return message.getChannel().flatMap(channel -> channel.createMessage(finalResponse));
                        case "gptRand":
                            config.setOpenAiTemperature(parseRandomnessValue(query));
                            final String result = "Randomness was set to " + config.getOpenAiTemperature();
                            return message.getChannel().flatMap(channel -> channel.createMessage(result));
                        case "gptModel":
                            config.setOpenAiModel(parseModelValue(query));
                            final String modelResult = "Model was set to " + config.getOpenAiModel();
                            return message.getChannel().flatMap(channel -> channel.createMessage(modelResult));
                        case "gptConfig":
                            final String configResult= "Current Configuration:\n" + config.openAiConfigToString();
                            return message.getChannel().flatMap(channel -> channel.createMessage(configResult));
                        case "gptHelp":
                            return message.getChannel().flatMap(channel ->
                                    channel.createMessage(getHelpText()));
                        default:
                            break;
                    }
                }
                return Mono.empty();
            }).then();

            return onLogin.and(onMessage);
        });

        login.block();
    }

    public static String getHelpText() {
        String text = "GPT Bot interfaces with openAi gpt-3, to deliver interesting chats with a powerful AI.\n\n";
        text += "both `!` and `/` characters can be used to issue commands to the bot.\n";
        text += "\t`!gpt`       Send a message to gpt, and see its response\n";
        text += "\t`!gptRand`   Changes the randomness of the response 0.0-1.0\n";
        text += "\t`!gptModel`  Changes the openAi Model used\n";
        text += "\t`!gptConfig` Shows current GPT Bot configuration\n";
        text += "\t`!gptHelp`   Generates list off commands\n";
        return text;
    }

    public static String parseModelValue(String message) {
        String modelConfig = config.getOpenAiModel();
        for(OpenAiHelper.OpenAiModels value: new ArrayList<OpenAiHelper.OpenAiModels>
                (EnumSet.allOf(OpenAiHelper.OpenAiModels.class))) {
            if (message.contains(value.getId())) {
                modelConfig = value.getId();
                break;
            }
        }

        return modelConfig;
    }

    public static double parseRandomnessValue(String message) {
        double temperature;
        message = message.replaceAll("[^\\d.]", "");
        try {
            temperature = Double.parseDouble(message);
            if(!(temperature <= 1.0) || !(temperature > 0)) {
                temperature = 0.7;
            }
        } catch(Exception e) {
            temperature = 0.7;
        }
        return temperature;
    }

    public static String parseCommand(String message) {
        StringBuilder command = new StringBuilder();
        for (int i = 1; i < message.length(); i++) {
            if(Character.isWhitespace(message.charAt(i))) {
                break;
            } else {
                command.append(message.charAt(i));
            }
        }

        return command.toString();
    }

    public static String parseQuery(String message) {
        int queryStartIndex = 0;
        for (int i = 1; i < message.length(); i++) {
            if (Character.isWhitespace(message.charAt(i))) {
                queryStartIndex= i + 1;
                break;
            }
        }

        return message.substring(queryStartIndex);
    }
}