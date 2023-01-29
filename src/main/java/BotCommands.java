public class BotCommands {




    public enum Commands {
        GPT("gpt",""),
        GPT_RANDOMNESS("gptRand",""),
        GPT_MODULE("gptModel",""),
        GPT_CONFIG("gptConfig",""),
        GPT_HELP("gptHelp","");

        private final String id;

        private final String helpText;

        Commands(final String id, final String helpText) {
            this.id = id;
            this.helpText = helpText;
        }

        public String getId() {
            return id;
        }

        public String getHelpText() {
            return helpText;
        }
        @Override
        public String toString() {
            return id;
        }
    }


    public static String getCommandHelpText() {
        String helpText = "";
        return helpText;
    }
}
