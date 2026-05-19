const { createTransporter } = require("../providers/emailProvider");

const buildOtpEmailHtml = (otp, purpose = "login") => {
  const actionText = purpose === "register" ? "sign-up" : "login";
  const vietnameseActionText = purpose === "register" ? "đăng ký" : "đăng nhập";

  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8">
        <style>
          body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
          }
          .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
          }
          .otp-box {
            background-color: #f0f0f0;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            margin: 20px 0;
          }
          .otp-code {
            font-size: 32px;
            font-weight: bold;
            letter-spacing: 5px;
            color: #007bff;
          }
          .divider {
            border-top: 2px solid #cccccc;
            margin: 30px 0;
          }
          .english-section {
            margin-bottom: 20px;
          }
          .vietnamese-section {
            margin-top: 20px;
          }
          .footer {
            font-size: 12px;
            color: #666;
            margin-top: 20px;
            text-align: center;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="english-section">
            <p>Hi there,</p>
            <p>Your OTP is:</p>
            <div class="otp-box">
              <div class="otp-code">${otp}</div>
            </div>
            <p>Please use the OTP to complete your ${actionText} process. OTP is valid for 5 minutes.</p>
            <p>If you didn't try to ${actionText} just now, please ignore this email.</p>
            <p>Feel free to contact us for any assistance by replying to this email.</p>
          </div>

          <div class="divider"></div>

          <div class="vietnamese-section">
            <p>Xin chào,</p>
            <p>Mã OTP của bạn là:</p>
            <div class="otp-box">
              <div class="otp-code">${otp}</div>
            </div>
            <p>Vui lòng sử dụng mã OTP này để hoàn tất quá trình ${vietnameseActionText}. Mã OTP có hiệu lực trong 5 phút.</p>
            <p>Nếu bạn không thực hiện yêu cầu ${vietnameseActionText} vừa rồi, vui lòng bỏ qua email này.</p>
            <p>Nếu cần hỗ trợ, bạn có thể phản hồi trực tiếp email này.</p>
          </div>

          <div class="footer">
            <p>BPMBoo Heart Beat</p>
          </div>
        </div>
      </body>
    </html>
  `;
};

const buildOtpEmailText = (otp, purpose = "login") => {
  const actionText = purpose === "register" ? "sign-up" : "login";
  const vietnameseActionText = purpose === "register" ? "đăng ký" : "đăng nhập";

  return `Hi there,

Your OTP is: ${otp}

Please use the OTP to complete your ${actionText} process. OTP is valid for 5 minutes.
If you didn't try to ${actionText} just now, please ignore this email.
Feel free to contact us for any assistance by replying to this email.

---

Xin chào,

Mã OTP của bạn là: ${otp}

Vui lòng sử dụng mã OTP này để hoàn tất quá trình ${vietnameseActionText}. Mã OTP có hiệu lực trong 5 phút.
Nếu bạn không thực hiện yêu cầu ${vietnameseActionText} vừa rồi, vui lòng bỏ qua email này.
Nếu cần hỗ trợ, bạn có thể phản hồi trực tiếp email này.

BPMBoo Heart Beat`;
};

const sendOtpEmail = async ({ to, otp, purpose = "login" }) => {
  const transporter = createTransporter();

  const subject =
    purpose === "register"
      ? "BPMBoo Heart Beat - Sign Up OTP"
      : "BPMBoo Heart Beat - Login OTP";

  const html = buildOtpEmailHtml(otp, purpose);
  const text = buildOtpEmailText(otp, purpose);

  try {
    console.log(`[Email] Sending ${purpose} OTP to ${to}...`);

    const result = await transporter.sendMail({
      from: `"BPMBoo Heart Beat" <${process.env.EMAIL_USER}>`,
      to,
      subject,
      text,
      html
    });

    console.log(`[Email] ✅ Email sent successfully to ${to}. MessageID: ${result.messageId}`);
    return true;
  } catch (error) {
    console.error(`[Email] ❌ Failed to send email to ${to}:`, error.message);
    console.error("[Email] Error code:", error.code);
    console.error("[Email] Error response:", error.response);
    throw error;
  }
};

module.exports = {
  sendOtpEmail
};