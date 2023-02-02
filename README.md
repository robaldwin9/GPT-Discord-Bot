# GPT-3-Discord-Bot
Discord bot enabling you to chat with openAI gpt-3 In Discord

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

## Supported Commands

both `!` and `/` characters can be used to issue commands to the bot.    

`!gpt` ->  Send a message to gpt, and get a text based reply

`!gptimage` -> send a message to gpt, and get an image as a response

 `!gptRand` ->   Changes the randomness of the response 0.0-2.0   

 `!gptModel` ->  Changes the openAi Model used    

`!gptConfig` -> Shows current GPT Bot configuration   

 `!gptHelp`  ->   Generates list off commands

## Dependencies

1. [openai-java](https://github.com/TheoKanning/openai-java)
2. [Discord4j](https://discord4j.com/)
