const { createTransporter } = require("../providers/emailProvider");

const buildOtpEmailHtml = (otp, purpose = "login") => {
  const actionText = purpose === "register" ? "sign-up" : "login";
  const vietnameseActionText = purpose === "register" ? "đăng ký" : "đăng nhập";

  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>BPMBoo Heart Beat OTP</title>
      </head>

      <body style="margin:0; padding:0; background-color:#f4f6f8; font-family:Arial, sans-serif; color:#333;">
        <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8; padding:24px 0;">
          <tr>
            <td align="center">
              <table width="100%" cellpadding="0" cellspacing="0" style="max-width:600px; background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                
                <tr>
                  <td style="background-color:#2e7d32; padding:24px; text-align:center;">
                    <h1 style="margin:0; color:#ffffff; font-size:24px;">
                      BPMBoo Heart Beat
                    </h1>
                  </td>
                </tr>

                <tr>
                  <td style="padding:28px;">
                    <div style="margin-bottom:24px;">
                      <p style="font-size:16px; margin:0 0 12px;">Hi there,</p>

                      <p style="font-size:16px; margin:0 0 12px;">
                        Your OTP code is:
                      </p>

                      <div style="background-color:#f0f7f1; border:1px solid #c8e6c9; border-radius:10px; text-align:center; padding:22px; margin:20px 0;">
                        <div style="font-size:34px; font-weight:bold; letter-spacing:6px; color:#2e7d32;">
                          ${otp}
                        </div>
                      </div>

                      <p style="font-size:15px; margin:0 0 10px;">
                        Please use this OTP to complete your ${actionText} process.
                      </p>

                      <p style="font-size:15px; margin:0 0 10px;">
                        This OTP is valid for <strong>5 minutes</strong>.
                      </p>

                      <p style="font-size:15px; margin:0;">
                        If you did not request this ${actionText}, please ignore this email.
                      </p>
                    </div>

                    <hr style="border:none; border-top:1px solid #dddddd; margin:28px 0;" />

                    <div>
                      <p style="font-size:16px; margin:0 0 12px;">Xin chào,</p>

                      <p style="font-size:16px; margin:0 0 12px;">
                        Mã OTP của bạn là:
                      </p>

                      <div style="background-color:#f0f7f1; border:1px solid #c8e6c9; border-radius:10px; text-align:center; padding:22px; margin:20px 0;">
                        <div style="font-size:34px; font-weight:bold; letter-spacing:6px; color:#2e7d32;">
                          ${otp}
                        </div>
                      </div>

                      <p style="font-size:15px; margin:0 0 10px;">
                        Vui lòng sử dụng mã OTP này để hoàn tất quá trình ${vietnameseActionText}.
                      </p>

                      <p style="font-size:15px; margin:0 0 10px;">
                        Mã OTP có hiệu lực trong <strong>5 phút</strong>.
                      </p>

                      <p style="font-size:15px; margin:0;">
                        Nếu bạn không thực hiện yêu cầu ${vietnameseActionText}, vui lòng bỏ qua email này.
                      </p>
                    </div>
                  </td>
                </tr>

                <tr>
                  <td style="background-color:#f8f8f8; padding:18px; text-align:center;">
                    <p style="margin:0; font-size:12px; color:#777;">
                      This is an automated email from BPMBoo Heart Beat.
                    </p>
                  </td>
                </tr>

              </table>
            </td>
          </tr>
        </table>
      </body>
    </html>
  `;
};

const buildOtpEmailText = (otp, purpose = "login") => {
  const actionText = purpose === "register" ? "sign-up" : "login";
  const vietnameseActionText = purpose === "register" ? "đăng ký" : "đăng nhập";

  return `BPMBoo Heart Beat

Hi there,

Your OTP code is: ${otp}

Please use this OTP to complete your ${actionText} process.
This OTP is valid for 5 minutes.

If you did not request this ${actionText}, please ignore this email.

---

Xin chào,

Mã OTP của bạn là: ${otp}

Vui lòng sử dụng mã OTP này để hoàn tất quá trình ${vietnameseActionText}.
Mã OTP có hiệu lực trong 5 phút.

Nếu bạn không thực hiện yêu cầu ${vietnameseActionText}, vui lòng bỏ qua email này.

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
    throw error;
  }
};

module.exports = {
  sendOtpEmail
};