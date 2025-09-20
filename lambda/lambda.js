export const handler = async (event) => {
  try {
    const { channelId, messageType, body } = event;

    if (!channelId) {
      return {
        statusCode: 400,
        body: JSON.stringify({ message: "channelId is required" }),
      };
    }

    if (!messageType) {
      return {
        statusCode: 400,
        body: JSON.stringify({ message: "messageType is required" }),
      };
    }

    const token = process.env.DISCORD_BOT_TOKEN;

    const MessageType = {
      ERROR: "ERROR",
      INFO: "INFO",
    };

    Object.freeze(MessageType);

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
        return {
          statusCode: 400,
          body: JSON.stringify({
            message: `Unknown messageType (type: ${messageType})`,
          }),
        };
    }

    // Call Discord API
    const res = await fetch(
      `https://discord.com/api/v10/channels/${channelId}/messages`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bot ${token}`,
        },
        body: JSON.stringify(payload),
      }
    );

    if (!res.ok) {
      const text = await res.text();
      console.error("Discord API Error:", text);
      return { statusCode: res.status, body: text };
    }

    return {
      statusCode: 200,
      body: JSON.stringify({ message: "Message sent" }),
    };
  } catch (err) {
    console.error("Lambda Error:", err);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "Internal server error" }),
    };
  }
};
