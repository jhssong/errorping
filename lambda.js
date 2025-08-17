export const handler = async (event) => {
  try {
    console.log("event is here\n");
    console.log(event);
    // const body = typeof event.body === "string" ? JSON.parse(event.body) : event.body;
    const { channelId, error, info } = event;

    if (!channelId) {
      return {
        statusCode: 400,
        body: JSON.stringify({ message: "channelId is required" }),
      };
    }

    const token = process.env.DISCORD_BOT_TOKEN;

    let payload;
    // Report error to discord server
    if (error) {
      payload = {
        embeds: [
          {
            title: `🚨 ${error.title || "Unknown Error"}`,
            description: error.detail || "*No detail provided.*",
            color: error.status >= 500 ? 0xff0000 : 0xffd700,
            fields: [
              { name: "Trace ID", value: error.traceId || "`N/A`" },
              {
                name: "Status Code",
                value: String(error.status || "NULL"),
                inline: true,
              },
              { name: "Method", value: error.method || "`N/A`", inline: true },
              { name: "Instance", value: error.instance || "`N/A`" },
            ],
            timestamp: new Date().toISOString(),
          },
        ],
      };
    }
    // Report info to discord server
    else if (info) {
      payload = {
        embeds: [
          {
            title: "📢 Server Notification",
            description: info.message || "*No message provided.*",
            color: 0x00bfff,
            timestamp: new Date().toISOString(),
          },
        ],
      };
    } else {
      return {
        statusCode: 400,
        body: JSON.stringify({
          message: "Either 'error' or 'info' is required",
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
