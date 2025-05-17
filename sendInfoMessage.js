import dotenv from "dotenv";
import { EmbedBuilder } from "discord.js";

function createInfoEmbed(issue) {
  const { message } = issue;

  const embed = new EmbedBuilder()
    .setColor(0xbdd2b6)
    .setTitle("📢 Server Notification")
    .setDescription(message || "*No message provided.*")
    .setTimestamp();

  return embed;
}

async function sendInfoMessage(discordChannel, info) {
  try {
    const embed = createInfoEmbed(info);
    await discordChannel.send({ embeds: [embed] });
    console.log(
      `🚀 Info sended to ${discordChannel.guild.name}/${
        discordChannel.name
      } ${discordChannel.toString()}`
    );
  } catch (e) {
    console.error("Error occured in sendInfoMessage.js");
    console.error(e);
    return false;
  }
  return true;
}

export default sendInfoMessage;
