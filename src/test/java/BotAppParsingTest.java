import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BotAppParsingTest {

    @Test
    public void testParseRandomnessValue() {
        // check parsing with incorrect input
        assertEquals(0.7 , BotApp.parseRandomnessValue("ehgerherhh"));
        assertEquals(0.7, BotApp.parseRandomnessValue("0.0") );

        // Test less than max
        assertEquals( 1.9, BotApp.parseRandomnessValue("1.9") );

        // Test Max
        assertEquals( 2.0, BotApp.parseRandomnessValue("2.0") );

    }

    @Test
    public void testParseCommand() {
        assertEquals("gpt", BotApp.parseCommand("!gpt what is 2 + 2"));
        assertEquals("gpt", BotApp.parseCommand("/gpt"));
        assertEquals("gptrole", BotApp.parseCommand("!gptrole you are helpful bott"));
        assertEquals("gptimage", BotApp.parseCommand("!gPtImAgE"));
    }

    @Test
    public void testParseQuery() {
        assertEquals("what is 5 + 5", BotApp.parseQuery("!gpt what is 5 + 5"));
        assertEquals("what is 5 + 5", BotApp.parseQuery("!gptrole what is 5 + 5"));
        assertEquals("what is 5 + 5", BotApp.parseQuery("!Nonesense what is 5 + 5"));
    }

    @Test
    public void testParseModel() {
        assertEquals("gpt-3.5-turbo", BotApp.parseQuery("!gptModel gpt-3.5-turbo"));
        assertEquals("text-curie-001", BotApp.parseQuery("!gptModel text-curie-001"));
        assertEquals("text-babbage-001", BotApp.parseQuery("!gptModel text-babbage-001"));
        assertEquals("text-ada-001", BotApp.parseQuery("!gptModel text-ada-001"));
        assertEquals("conde-davinci-002", BotApp.parseQuery("!gptModel conde-davinci-002"));
        assertEquals("code-chsman-001", BotApp.parseQuery("!gptModel code-chsman-001"));
        assertEquals("text-davinci-003", BotApp.parseQuery("!gptModel text-davinci-003"));
    }
}
