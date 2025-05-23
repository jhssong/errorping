import dotenv from "dotenv";
import { EmbedBuilder } from "discord.js";

import { getColorByStatus, getStackTraceFile } from "./util/index.js";

dotenv.config();

function createErrorEmbed(error) {
  const { traceId, type, title, status, detail, instance, method } = error;

  const embed = new EmbedBuilder()
    .setColor(getColorByStatus(status))
    .setTitle(`🚨 ${title || "Unknown Error"}`)
    .setDescription(detail || "*No detail provided.*")
    .addFields(
      {
        name: "Trace ID",
        value: traceId ? `\`${traceId}\`` : "`N/A`",
        inline: false,
      },
      {
        name: "Type",
        value: type ? `\`${type}\`` : "`N/A`",
        inline: false,
      },
      {
        name: "Method",
        value: method ? `\`${method.toUpperCase()}\`` : "`N/A`",
        inline: true,
      },
      {
        name: "Status Code",
        value: `\`${status || "NULL"}\``,
        inline: true,
      },
      {
        name: "Instance",
        value: instance ? `\`${instance}\`` : "`N/A`",
        inline: false,
      }
    )
    .setTimestamp();

  return embed;
}

async function reportError(discordChannel, error, trace) {
  try {
    const embed = createErrorEmbed(error);
    await discordChannel.send({ embeds: [embed] });
    console.log(
      `🚀 Error reported to ${discordChannel.guild.name}/${
        discordChannel.name
      } ${discordChannel.toString()}`
    );

    // Send stack trace if exists
    if (trace) {
      if (trace.length > 1000) {
        // Send file if stack trace is too long
        const file = await getStackTraceFile();
        await discordChannel.send({ files: [file] });
      } else {
        await discordChannel.send(`\`\`\`\n${trace}\n\`\`\``);
      }
    }
  } catch (e) {
    console.error("Error occured in reportError.js");
    console.error(e);
    return false;
  }
  return true;
}

export default reportError;
