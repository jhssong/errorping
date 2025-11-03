import dotenv from "dotenv";
import express from "express";
import { Client, GatewayIntentBits } from "discord.js";
import winston from "winston";

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;
app.use(express.json());

const apiKeys = JSON.parse(process.env.API_KEYS_JSON || "{}");

export const apiKeyMiddleware = (req, res, next) => {
  const clientApiKey = req.headers["x-api-key"];
  const keyInfo = apiKeys[clientApiKey];

  if (!keyInfo) {
    winston.warn(`❌ Unauthorized request from ${req.ip}`);
    return res.status(401).json({ message: "Invalid API Key" });
  }

  req.apiKeyInfo = keyInfo;
  next();
};

const logger = winston.createLogger({
  level: "info",
  format: winston.format.combine(
    winston.format.timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
    winston.format.printf(({ timestamp, level, message }) => {
      return `[${timestamp}] [${level.toUpperCase()}] ${message}`;
    })
  ),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: "logs/combined.log" }),
    new winston.transports.File({ filename: "logs/error.log", level: "error" }),
  ],
});

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
  logger.info(`✅ Bot is ready (${client.user.tag})`);
});

app.post("/", apiKeyMiddleware, async (req, res) => {
  const requestId = Math.random().toString(36).substring(2, 8);
  const { messageType, body } = req.body;
  const { user, channelId } = req.apiKeyInfo;

  logger.info(
    `[${requestId}] Incoming POST / - type: ${messageType}, user: ${user}`
  );

  try {
    // Check channelId exists in request
    if (!channelId) {
      logger.warn(`[${requestId}] Missing channelId`);
      return res.status(400).json({ message: "channelId is not set" });
    }

    // Check messageType exists in request
    if (!messageType) {
      logger.warn(`[${requestId}] Missing messageType`);
      return res.status(400).json({ message: "messageType is not set" });
    }

    const discordChannel = await client.channels.fetch(channelId);

    // Check channelId is valid
    if (!discordChannel || !discordChannel.isTextBased()) {
      logger.warn(`[${requestId}] Invalid channel ID: ${channelId}`);
      return res.status(400).json({ message: "Invalid channel ID" });
    }

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
        logger.warn(`[${requestId}] Unknown messageType: ${messageType}`);
        return res
          .status(400)
          .json({ message: `Unknown messageType (type: ${messageType})` });
    }

    await discordChannel.send(payload);
    logger.info(`[${requestId}] ✅ Message sent for user: ${user}`);
    return res.status(200).json({ message: "Message sent to Discord" });
  } catch (err) {
    logger.error(
      `[${requestId}] ❌ Error sending message: ${err.stack || err.message}`
    );
    return res.status(500).json({ message: "Internal server body" });
  }
});

app.listen(PORT, () => logger.info(`🟢 Server running on port ${PORT}`));
