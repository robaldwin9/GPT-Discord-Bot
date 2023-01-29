import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class Config {
    final static String programAbsolutePath = "";
    private static final Logger logger = LogManager.getLogger(Config.class);
    private static Config instance;

    private final String botToken;

    private final String gptToken;

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

    public double getOpenAiTemperature() {
        return openAiTemperature;
    }

    public void setOpenAiTemperature(double temperature) {
        openAiTemperature = temperature;
    }

    public void setOpenAiModel(String model) {
        openAiModel = model;
    }

    public String getOpenAiModel() {
        return openAiModel;
    }

    public int getOpenAiMaxTokens() {
        return openAiMaxTokens;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getGptToken() {
        return gptToken;
    }

    public char getCommandCharacter() {
        return commandCharacter;
    }

    public static Config getInstance() {
        return instance == null ? (instance = new Config()): instance;
    }

    public String openAiConfigToString() {
        String configuration = "";
        configuration += "\tRandomness:  " + getOpenAiTemperature() + "\n";
        configuration += "\tToken Limit: " + getOpenAiMaxTokens() + "\n";
        configuration += "\tAi Model:    " + getOpenAiModel() + "\n";
        return configuration;
    }
}
