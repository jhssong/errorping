import dotenv from "dotenv";
import express from "express";
import { Client, GatewayIntentBits } from "discord.js";
import reportError from "./reportError.js";

dotenv.config();

const app = express();
const PORT = process.env.PORT;
app.use(express.json()); // Add body parser

// Create discord bot instance
const client = new Client({
  intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages],
});

// Bot login
client.login(process.env.DISCORD_TOKEN);

client.once("ready", () => {
  console.log(`✅ Bot is ready (${client.user.tag})`);
});

/**
 * Endpoint for sending server errors to the Discord channel
 *
 * Expected JSON request body format:
 * {
 *   "discordChannelId": "1332606213506072669",             // Discord text channel ID
 *   "error": {
 *     "traceId": "1"                                       // Trace ID
 *     "type": "https://myapi.com/docs/errors/auth-failed"  // URI reference
 *     "title": "Authentication Failed",                    // Error title
 *     "status": 401,                                       // HTTP status code
 *     "detail": "An invalid API key was provided.",        // Detailed error message
 *     "instance": "/api/v2/users/me"                       // API path where the error occurred
 *     "method": "POST"                                     // API method
 *   },
 *   "trace": "..." // (Optional) stack trace string; if too long, it will be sent as a file
 * }
 */
app.post("/report-error", async (req, res) => {
  const { discordChannelId, error, trace } = req.body;

  // Check discordChannelId exists in request
  if (!discordChannelId)
    return res.status(400).json({ message: "discordChannelId is not set" });

  const discordChannel = await client.channels.fetch(discordChannelId);

  // Check discordChannelId is valid
  if (!discordChannel || !discordChannel.isTextBased())
    return res.status(400).json({ message: "Invalid channel ID" });

  // Send error message to the discord channel
  const isSent = await reportError(discordChannel, error, trace);
  if (isSent)
    return res.status(200).json({ message: "Message sent to Discord channel" });
  else return res.status(500).json({ message: "Unknown Error occured" });
});

app.listen(PORT, () => console.log("🚀 Server running on port", PORT));
