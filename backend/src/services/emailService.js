const createMailer = require("../config/mailer");

const sendOtpEmail = async (email, otp) => {
  const transporter = createMailer();

  const mailOptions = {
    from: `"BPMBoo" <${process.env.EMAIL_USER}>`,
    to: email,
    subject: "Your BPMBoo Login OTP",
    html: `
      <div style="font-family: Arial, sans-serif; line-height: 1.6;">
        <h2>BPMBoo Login Verification</h2>
        <p>Hello,</p>
        <p>Your OTP code is:</p>
        <h1 style="letter-spacing: 4px;">${otp}</h1>
        <p>This code will expire in ${process.env.OTP_EXPIRE_MINUTES || 5} minutes.</p>
        <p>If you did not request this code, please ignore this email.</p>
      </div>
    `
  };

  await transporter.sendMail(mailOptions);
};

module.exports = {
  sendOtpEmail
};