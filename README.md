# GPT-4-Discord-Bot
Discord bot enabling you to chat with openAI gpt-3 In Discord. Default model used is `gpt-4` which allows the bot to respond based on past chat history. Additionally it is possible to configure the bots personality either by command, or configuration.

## Setup

1. Edit `config.properties`, and add both your discord token, and your open AI token
   ```
   botToken=ENTER_DISCORD_TOKEN
   gptToken=ENTER_GPT_TOKEN
   ```

   - [Guide](https://discordpy.readthedocs.io/en/stable/discord.html) For Creating Discord Bot, which will give you your `botToken`
     - Under bot settings ensure you have Message Content Intent enabled
     - When generating bot URL you need the following permissions, and ensure scope is bot.
       1. Read Messages/View channels
       2. Manage Messages
   - [openAI](https://openai.com/) site where you will create an account, and get your `gptToken`

2. Build the application with `mvn clean install`

3. Copy `gptBot.jar`, `log4j2.xml`, and `config.properties` to the directory you wish to deploy the bot to, and run with `java -jar gptBot.jar`

### Moderation

Discord bot automatically runs a moderation request on all openAI prompts, to ensure this application does not violate OpenAi's content policy.

## Supported Commands

both `!` and `/` characters can be used to issue commands to the bot.    

`!gpt` ->  Send a message to gpt, and get a text based reply

`!gptimage` -> send a message to gpt, and get an image as a response

 `!gptRand` ->   Changes the randomness of the response 0.0-2.0   

 `!gptModel` ->  Changes the openAi Model used    

`!gptConfig` -> Shows current GPT Bot configuration   

 `!gptHelp`  ->   Generates list off commands

`!gptRole` -> used with 3.5 turbo model, affects the bots personality and is sent with every chat completion.

## Dependencies

1. [openai-java](https://github.com/TheoKanning/openai-java)
2. [Discord4j](https://discord4j.com/)
