package bot.config;

import bot.ai.AiChatMessages;
import discord4j.common.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfig {
    // log4j logging
    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    // OpenAi Configuration for requests
    private String openAiModel;
    private final int openAiMaxTokens;
    private double openAiTemperature;
    private String botPersonality;

    // Server Data
    private final Snowflake channelSnowFlake;
    private final AiChatMessages messages;


    public ServerConfig(Snowflake channelSnowFlake) {
        this.channelSnowFlake = channelSnowFlake;
        Config config= Config.getInstance();
        openAiMaxTokens = config.getOpenAiMaxTokens();
        openAiModel = config.getOpenAiModel();
        openAiTemperature = config.getOpenAiTemperature();
        botPersonality = config.getBotPersonality();
        messages = new AiChatMessages(config.getOpenAiMaxTokens(), config.getBotPersonality());
    }

    /**
     *
     * @return Max tokens allowed for OpenAi Response
     */
    public int getOpenAiMaxTokens() {
        return openAiMaxTokens;
    }

    /**
     *
     * @return current OpenAi temperature being used for requests
     */
    public double getOpenAiTemperature() {
        return openAiTemperature;
    }

    /**
     *
     * @param temperature OpenAi temperature to be used
     */
    public void setOpenAiTemperature(double temperature) {
        openAiTemperature = temperature;
    }

    /**
     *
     * @return current OpenAi Model in use
     */
    public String getOpenAiModel() {
        return openAiModel;
    }

    /**
     *
     * @param model OpenAi Model to be used
     */
    public void setOpenAiModel(String model) {
        openAiModel = model;
    }

    /**
     *
     * @return personality used for chat completions gpt3.5
     */
    public String getBotPersonality() {
        return  botPersonality;
    }

    /**
     *
     * @param botPersonality defined personality for gpt3.5
     */
    public void setBotPersonality(String botPersonality) {
        this.botPersonality = botPersonality;
    }

    /**
     *
     * @return conversation with gpt for selected server
     */
    public AiChatMessages getChatMessages() {
        return messages;
    }

    /**
     *
     * @return Server Identification
     */
    public Snowflake getChannelSnowFlake() {
        return channelSnowFlake;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ServerConfig) {
            return super.equals(obj) && ((ServerConfig)(obj)).getChannelSnowFlake().equals(getChannelSnowFlake());
        } else {
            return false;
        }
    }
}
