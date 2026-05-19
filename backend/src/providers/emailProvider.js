const createTransporter = () => {
  return {
    sendMail: async ({ to, subject, text, html }) => {
      if (!process.env.BREVO_API_KEY) {
        throw new Error("BREVO_API_KEY is required");
      }

      const senderEmail = process.env.BREVO_SENDER_EMAIL || process.env.EMAIL_USER;

      if (!senderEmail) {
        throw new Error("BREVO_SENDER_EMAIL or EMAIL_USER is required");
      }

      const emailData = {
        sender: {
          name: process.env.BREVO_SENDER_NAME || "BPMBoo Heart Beat",
          email: senderEmail,
        },
        to: [
          {
            email: to,
          },
        ],
        subject,
        textContent: text,
        htmlContent: html,
      };

      const response = await fetch("https://api.brevo.com/v3/smtp/email", {
        method: "POST",
        headers: {
          accept: "application/json",
          "api-key": process.env.BREVO_API_KEY,
          "content-type": "application/json",
        },
        body: JSON.stringify(emailData),
      });

      const data = await response.json();

      console.log("[Brevo Provider] status:", response.status);
      console.log("[Brevo Provider] response:", data);

      if (!response.ok) {
        throw new Error(data.message || "Failed to send email with Brevo");
      }

      return {
        messageId: data.messageId || "brevo-message-sent",
        response: data,
      };
    },
  };
};

module.exports = {
  createTransporter,
};