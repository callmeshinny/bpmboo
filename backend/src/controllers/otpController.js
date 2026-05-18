const OtpCode = require("../models/OtpCode");
const User = require("../models/User");

const {
  generateOtp,
  hashOtp,
  compareOtp,
  getOtpExpiryDate
} = require("../services/otpService");

const { sendOtpEmail } = require("../services/emailService");

const requestOtp = async (req, res) => {
  try {
    const email = String(req.body.email).trim().toLowerCase();
    const purpose = req.body.purpose || "login";

    if (purpose === "register") {
      const user = await User.findOne({ email });

      if (!user) {
        return res.status(404).json({
          success: false,
          message: "User not found. Please register first."
        });
      }

      if (user.isEmailVerified) {
        return res.status(200).json({
          success: true,
          message: "Email is already verified."
        });
      }
    }

    if (purpose === "login") {
      const user = await User.findOne({ email });

      if (!user) {
        return res.status(404).json({
          success: false,
          message: "User not found."
        });
      }
    }

    await OtpCode.updateMany(
      {
        email,
        purpose,
        isUsed: false
      },
      {
        isUsed: true
      }
    );

    const otp = generateOtp();
    const otpHash = await hashOtp(otp);

    await OtpCode.create({
      email,
      purpose,
      otpHash,
      expiresAt: getOtpExpiryDate()
    });

    await sendOtpEmail({
      to: email,
      otp,
      purpose
    });

    return res.status(200).json({
      success: true,
      message: "OTP has been sent to your email."
    });
  } catch (error) {
    console.error("Request OTP error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to send OTP.",
      error: error.message
    });
  }
};

const verifyOtpCode = async (req, res) => {
  try {
    const email = String(req.body.email).trim().toLowerCase();
    const otp = String(req.body.otp).trim();
    const purpose = req.body.purpose || "login";

    const otpRecord = await OtpCode.findOne({
      email,
      purpose,
      isUsed: false,
      expiresAt: { $gt: new Date() }
    }).sort({ createdAt: -1 });

    if (!otpRecord) {
      return res.status(400).json({
        success: false,
        message: "OTP is invalid or expired."
      });
    }

    const isMatch = await compareOtp(otp, otpRecord.otpHash);

    if (!isMatch) {
      return res.status(400).json({
        success: false,
        message: "OTP is incorrect."
      });
    }

    otpRecord.isUsed = true;
    await otpRecord.save();

    if (purpose === "register") {
      await User.findOneAndUpdate(
        { email },
        { isEmailVerified: true },
        { new: true }
      );
    }

    return res.status(200).json({
      success: true,
      message: "OTP verified successfully."
    });
  } catch (error) {
    console.error("Verify OTP error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to verify OTP.",
      error: error.message
    });
  }
};

module.exports = {
  requestOtp,
  verifyOtpCode
};