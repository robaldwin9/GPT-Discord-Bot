import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class Config {
    // log4j logging
    private static final Logger logger = LogManager.getLogger(Config.class);

    // Singleton
    private static Config instance;

    // Discord bot token
    private final String botToken;

    // OpenaAi token
    private final String gptToken;

    // OpenAi Configuration for requests
    private String openAiModel;
    private final int openAiMaxTokens;
    private  double openAiTemperature;
    private final char commandCharacter;

    private Config() {
        try {
            Properties config = new Properties();
            String dir = new File(BotApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
            config.load(new FileInputStream(dir + "/config.properties"));
            botToken = config.getProperty("botToken");
            gptToken = config.getProperty("gptToken");
            commandCharacter = config.getProperty("commandCharacter").charAt(0);
            openAiMaxTokens = Integer.parseInt(config.getProperty("openAiMaxTokens"));
            openAiModel = config.getProperty("openAiModel");
            openAiTemperature = Double.parseDouble(config.getProperty("openAiTemperature"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
     * @param model OpenAi Model to be used
     */

    public void setOpenAiModel(String model) {
        openAiModel = model;
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
     * @return Max tokens allowed for OpenAi Response
     */
    public int getOpenAiMaxTokens() {
        return openAiMaxTokens;
    }

    /**
     *
     * @return Discord Token
     */
    public String getBotToken() {
        return botToken;
    }

    /**
     *
     * @return OpenAi Token
     */
    public String getGptToken() {
        return gptToken;
    }

    /**
     *
     * @return Character to indicate bot command
     */
    public char getCommandCharacter() {
        return commandCharacter;
    }


    /**
     *
     * @return config instance
     */
    public static Config getInstance() {
        return instance == null ? (instance = new Config()): instance;
    }

    @Override
    public String toString() {
        String configuration = "";
        configuration += "\tRandomness:  " + getOpenAiTemperature() + "\n";
        configuration += "\tToken Limit: " + getOpenAiMaxTokens() + "\n";
        configuration += "\tAi Model:    " + getOpenAiModel() + "\n";
        return configuration;
    }
}
