const { generateOtp, saveOtp, verifyOtp } = require("../services/otpService");
const { sendOtpEmail } = require("../services/emailService");

const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

const requestOtp = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email || !isValidEmail(email)) {
      return res.status(400).json({
        success: false,
        message: "Valid email is required."
      });
    }

    const otp = generateOtp();

    saveOtp(email, otp);

    await sendOtpEmail(email, otp);

    return res.status(200).json({
      success: true,
      message: "OTP has been sent to your email."
    });
  } catch (error) {
    console.error("Request OTP error:", error);

    return res.status(500).json({
      success: false,
      message: "Failed to send OTP."
    });
  }
};

const verifyOtpCode = (req, res) => {
  try {
    const { email, otp } = req.body;

    if (!email || !isValidEmail(email)) {
      return res.status(400).json({
        success: false,
        message: "Valid email is required."
      });
    }

    if (!otp) {
      return res.status(400).json({
        success: false,
        message: "OTP is required."
      });
    }

    const result = verifyOtp(email, otp);

    if (!result.success) {
      return res.status(400).json(result);
    }

    return res.status(200).json({
      success: true,
      message: "Login successful.",
      user: {
        email
      },
      token: "temporary-login-token-for-testing"
    });
  } catch (error) {
    console.error("Verify OTP error:", error);

    return res.status(500).json({
      success: false,
      message: "Failed to verify OTP."
    });
  }
};

module.exports = {
  requestOtp,
  verifyOtpCode
};