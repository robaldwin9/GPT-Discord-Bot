package bot.ai;

import com.theokanning.openai.completion.chat.ChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class AiChatMessages extends ArrayList<ChatMessage> {
    private static final Logger logger = LogManager.getLogger(AiChatMessages.class);
    public static final String SYSTEM_ROLE = "system";
    public static final String ASSISTANT_ROLE = "assistant";
    public static final String USER_ROLE = "user";
    private final int charLimit;
    private int charCount;
    public static final int charBuffer = 5;

    public AiChatMessages(int tokenLimit, String botPersonality) {
        ChatMessage systemMessage = new ChatMessage(SYSTEM_ROLE, botPersonality);
        this.charLimit = tokenLimit* 4;
        charCount += getChatMessageCharCount(systemMessage);
        super.add(systemMessage);
    }

    @Override
    public boolean add(ChatMessage chatMessage) {
        charCount += getChatMessageCharCount(chatMessage);
        var result = super.add(chatMessage);

        while (charCount > charLimit) {
            removeOldestExchange();
        }

        return result;
    }

    public int getCharCount() {
        return charCount;
    }

    public int getChatMessageCharCount(ChatMessage message) {
      return message.getRole().length() + message.getContent().length() + charBuffer;
    }

    /**
     * Remove some history to make more room
     */
    public void removeOldestExchange() {
        int userMessagesRemoved = 0;
        int assistantMessagesRemoved = 0;

            for (int i =0; i < size(); i ++) {

                ChatMessage message = get(i);
                if (userMessagesRemoved > 0 && assistantMessagesRemoved > 0) {
                    return;
                }
                if (message.getRole().equals(USER_ROLE) && userMessagesRemoved == 0) {
                    remove(message);
                    userMessagesRemoved += 1;
                } else if (message.getRole().equals(ASSISTANT_ROLE) && assistantMessagesRemoved == 0) {
                    remove(message);
                    assistantMessagesRemoved += 1;
                }
            }
    }

    @Override
    public boolean remove(Object o) {
        charCount -= getChatMessageCharCount((ChatMessage) o);
        return super.remove(o);
    }

    @Override
    public ChatMessage remove(int index) {
        charCount -= getChatMessageCharCount(this.get(index));
        return super.remove(index);
    }

    public void emptyChat() {
        charCount = getChatMessageCharCount(this.get(0));
        this.removeIf(message -> message.getRole().equals(USER_ROLE) || message.getRole().equals(ASSISTANT_ROLE));
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("AiChatMessages charCount: ").append(charCount).append("\n");
        return output.append(super.toString()).toString();
    }
}
