// import dotenv from "dotenv";
// import fetch from "node-fetch";
// import { WebhookClient } from "discord.js";

// dotenv.config();

// const webhook = new WebhookClient({ url: process.env.DISCORD_WEBHOOK_URL });

// const endpoints = ["https://your-api.com/ping"];

// setInterval(async () => {
//   for (let url of endpoints) {
//     try {
//       const res = await fetch(url);
//       if (!res.ok) throw new Error("Invalid response");

//       const text = await res.text();
//       if (text !== "pong") throw new Error("Unexpected response: " + text);
//     } catch (err) {
//       await webhook.send(`⚠️ [PING 실패] ${url}\n오류: ${err.message}`);
//     }
//   }
// }, 5 * 60 * 1000);
