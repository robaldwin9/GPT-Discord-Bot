import bot.ai.AiChatMessages;
import com.theokanning.openai.completion.chat.ChatMessage;
import bot.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class AiChatMessagesTest {

    private AiChatMessages messages;

    private int startingCharCount;

    @BeforeEach
    public void setup() {
        Config config = Config.getInstance();
        messages = new AiChatMessages(config.getOpenAiMaxTokens(), config.getBotPersonality());
        startingCharCount = messages.getChatMessageCharCount(messages.get(0));
    }

    @Test
    public void testInitialization() {
        assertEquals(messages.getCharCount(), startingCharCount);
        assertEquals(messages.size(), 1);
    }

    @Test
    public void testAddingElement() {

        // Verify insertion
        messages.add(new ChatMessage(AiChatMessages.ASSISTANT_ROLE, "hi"));
        assertEquals(2, messages.size());

        // Verify char count equal to previous message, plus new message including buffer value
        assertEquals(messages.getChatMessageCharCount
                (messages.get(0)) + AiChatMessages.ASSISTANT_ROLE.length() + 2 + AiChatMessages.charBuffer,
                messages.getCharCount());
    }

    @Test
    public void testRemovingElement() {
        messages.add(new ChatMessage(AiChatMessages.ASSISTANT_ROLE, "hi"));
        assertEquals(2, messages.size());

        // Verify removal
        messages.remove(1);
        assertEquals(1, messages.size());
        assertEquals(startingCharCount, messages.getCharCount());
    }


    @Test
    public void testEmptyList() {
        int additions = 3;
        for (int i = 0; i < additions; i ++) {
            messages.add(new ChatMessage(AiChatMessages.USER_ROLE, "hi"));
            messages.add(new ChatMessage(AiChatMessages.ASSISTANT_ROLE, "hi"));
        }

        assertEquals(2 * additions + 1, messages.size());
        messages.emptyChat();
        assertEquals(1, messages.size());
        assertEquals(AiChatMessages.SYSTEM_ROLE, messages.get(0).getRole());
    }

    @Test
    public void testRemoveOldExchange() {
        int additions = 3;
        for (int i = 0; i < additions; i ++) {
            messages.add(new ChatMessage(AiChatMessages.USER_ROLE, "hi"));
            messages.add(new ChatMessage(AiChatMessages.ASSISTANT_ROLE, "hi"));
        }

        messages.removeOldestExchange();
        assertEquals(2 * additions + 1 - 2, messages.size());
    }
}
