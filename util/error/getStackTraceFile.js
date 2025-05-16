export default async function getStackTraceFile() {
  const { AttachmentBuilder } = await import("discord.js");
  const now = new Date(
    new Date().toLocaleString("en-US", { timeZone: "Asia/Seoul" })
  );

  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hour = String(now.getHours()).padStart(2, "0");
  const minute = String(now.getMinutes()).padStart(2, "0");
  const second = String(now.getSeconds()).padStart(2, "0");

  const fileName = `stack_trace_${year}${month}${day}_${hour}${minute}${second}`;

  const file = new AttachmentBuilder(Buffer.from(trace, "utf-8"), {
    name: fileName + ".log",
  });

  return file;
}
