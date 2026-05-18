const { createTransporter } = require("../providers/emailProvider");

const buildOtpEmailText = (otp, purpose = "login") => {
  const actionText = purpose === "register" ? "sign-up" : "login";
  const vietnameseActionText = purpose === "register" ? "đăng ký" : "đăng nhập";

  return `Hi there,

Your OTP is: ${otp}

Please use the OTP to complete your ${actionText} process. OTP is valid for 5 minutes.
If you didn't try to ${actionText} just now, please ignore this email.
Feel free to contact us for any assistance by replying to this email.

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

  const text = buildOtpEmailText(otp, purpose);

  await transporter.sendMail({
    from: `"BPMBoo Heart Beat" <${process.env.EMAIL_USER}>`,
    to,
    subject,
    text
  });

  return true;
};

module.exports = {
  sendOtpEmail
};