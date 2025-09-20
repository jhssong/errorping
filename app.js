import dotenv from "dotenv";
import express from "express";
import { Client, GatewayIntentBits } from "discord.js";

dotenv.config();

const app = express();
const PORT = process.env.PORT;
app.use(express.json());

const client = new Client({
  intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages],
});

const MessageType = {
  ERROR: "ERROR",
  INFO: "INFO",
};

Object.freeze(MessageType);

client.login(process.env.DISCORD_BOT_TOKEN);

client.once("ready", () => {
  console.log(`✅ Bot is ready (${client.user.tag})`);
});

app.post("/", async (req, res) => {
  try {
    const { channelId, messageType, body } = req.body;

    // Check channelId exists in request
    if (!channelId)
      return res.status(400).json({ message: "channelId is not set" });

    if (!messageType)
      return res.status(400).json({ message: "messageType is not set" });

    const discordChannel = await client.channels.fetch(channelId);

    // Check channelId is valid
    if (!discordChannel || !discordChannel.isTextBased())
      return res.status(400).json({ message: "Invalid channel ID" });

    let payload;
    switch (messageType) {
      case MessageType.ERROR:
        payload = {
          embeds: [
            {
              title: `🚨 ${body.title || "Unknown Error"}`,
              description: body.detail || "*No detail provided.*",
              color: body.status >= 500 ? 0xff0000 : 0xffd700,
              fields: [
                {
                  name: "Status Code",
                  value: String(body.status || "NULL"),
                  inline: true,
                },
                { name: "Method", value: body.method || "`N/A`", inline: true },
                { name: "Instance", value: body.instance || "`N/A`" },
              ],
              timestamp: (body.timestamp
                ? new Date(body.timestamp)
                : new Date()
              ).toISOString(),
            },
          ],
        };
        break;
      case MessageType.INFO:
        payload = {
          embeds: [
            {
              title: `📢 ${body.title || "Server Info"}`,
              description: body.message || "*No message provided.*",
              color: 0x00bfff,
              timestamp: (body.timestamp
                ? new Date(body.timestamp)
                : new Date()
              ).toISOString(),
            },
          ],
        };
        break;
      default:
        return res
          .status(400)
          .json({ message: `Unknown messageType (type: ${messageType})` });
    }

    await discordChannel.send(payload);

    return res.status(200).json({ message: "Message sent to Discord" });
  } catch (err) {
    console.error("Error sending message:", err);
    return res.status(500).json({ message: "Internal server body" });
  }
});

app.listen(PORT, () => console.log("🟢 Server running on port", PORT));
