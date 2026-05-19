const jwt = require("jsonwebtoken");
const User = require("../models/User");
const generateOtp = require("../utils/generateOtp");
const { sendOtpEmail } = require("../services/emailService");

const generateToken = (userId) => {
  return jwt.sign(
    { id: userId },
    process.env.JWT_SECRET,
    { expiresIn: "7d" }
  );
};

// REGISTER: create account + send OTP
const register = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({
        success: false,
        message: "Name, email and password are required",
      });
    }

    if (password.length < 6) {
      return res.status(400).json({
        success: false,
        message: "Password must be at least 6 characters",
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    const existingUser = await User.findOne({ email: normalizedEmail });

    if (existingUser && (existingUser.isVerified || existingUser.isEmailVerified)) {
      return res.status(400).json({
        success: false,
        message: "Email is already registered",
      });
    }

    const otp = generateOtp();
    const otpExpires = new Date(Date.now() + 5 * 60 * 1000);

    let user;

    // If user registered but not verified, update info and send new OTP
    if (existingUser && !existingUser.isVerified && !existingUser.isEmailVerified) {
      existingUser.name = name;
      existingUser.email = normalizedEmail;
      existingUser.password = password;
      existingUser.otp = otp;
      existingUser.otpExpires = otpExpires;
      existingUser.isVerified = false;
      existingUser.isEmailVerified = false;

      user = await existingUser.save();
    } else {
      user = await User.create({
        name,
        email: normalizedEmail,
        password,
        isVerified: false,
        isEmailVerified: false,
        otp,
        otpExpires,
      });
    }

    // Send OTP email without blocking frontend response
    sendOtpEmail({
      to: normalizedEmail,
      otp,
      purpose: "register",
    })
      .then(() => {
        console.log(`[Register] OTP email sent to ${normalizedEmail}`);
      })
      .catch((err) => {
        console.error(`[Register] Failed to send OTP email to ${normalizedEmail}:`, err.message);
      });

    return res.status(201).json({
      success: true,
      message: "Register successfully. OTP is being sent to your email.",
      email: user.email,
    });
  } catch (error) {
    console.error("Register error:", error);
    console.error("Register error message:", error.message);
    console.error("Register error stack:", error.stack);

    return res.status(500).json({
      success: false,
      message: "Server error during registration",
      error: error.message,
    });
  }
};

// VERIFY OTP
const verifyOtp = async (req, res) => {
  try {
    const { email, otp } = req.body;

    if (!email || !otp) {
      return res.status(400).json({
        success: false,
        message: "Email and OTP are required",
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    const user = await User.findOne({ email: normalizedEmail });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    if (user.isVerified || user.isEmailVerified) {
      const token = generateToken(user._id);

      return res.status(200).json({
        success: true,
        message: "User is already verified",
        token,
        user: {
          id: user._id,
          _id: user._id,
          name: user.name,
          email: user.email,
          isVerified: user.isVerified,
          isEmailVerified: user.isEmailVerified,
        },
      });
    }

    if (!user.otp || !user.otpExpires) {
      return res.status(400).json({
        success: false,
        message: "No OTP found. Please request a new OTP.",
      });
    }

    if (user.otp !== otp) {
      return res.status(400).json({
        success: false,
        message: "Invalid OTP",
      });
    }

    if (user.otpExpires < new Date()) {
      return res.status(400).json({
        success: false,
        message: "OTP has expired. Please request a new OTP.",
      });
    }

    user.isVerified = true;
    user.isEmailVerified = true;
    user.otp = undefined;
    user.otpExpires = undefined;

    await user.save();

    const token = generateToken(user._id);

    return res.status(200).json({
      success: true,
      message: "Email verified successfully",
      token,
      user: {
        id: user._id,
        _id: user._id,
        name: user.name,
        email: user.email,
        isVerified: user.isVerified,
        isEmailVerified: user.isEmailVerified,
      },
    });
  } catch (error) {
    console.error("Verify OTP error:", error);
    console.error("Verify OTP error message:", error.message);

    return res.status(500).json({
      success: false,
      message: "Server error during OTP verification",
      error: error.message,
    });
  }
};

// RESEND OTP
const resendOtp = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({
        success: false,
        message: "Email is required",
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    const user = await User.findOne({ email: normalizedEmail });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    if (user.isVerified || user.isEmailVerified) {
      return res.status(400).json({
        success: false,
        message: "User is already verified",
      });
    }

    const otp = generateOtp();
    const otpExpires = new Date(Date.now() + 5 * 60 * 1000);

    user.otp = otp;
    user.otpExpires = otpExpires;

    await user.save();

    // Send OTP email without blocking frontend response
    sendOtpEmail({
      to: normalizedEmail,
      otp,
      purpose: "register",
    })
      .then(() => {
        console.log(`[Resend OTP] OTP email sent to ${normalizedEmail}`);
      })
      .catch((err) => {
        console.error(`[Resend OTP] Failed to send OTP email to ${normalizedEmail}:`, err.message);
      });

    return res.status(200).json({
      success: true,
      message: "New OTP is being sent to your email",
    });
  } catch (error) {
    console.error("Resend OTP error:", error);
    console.error("Resend OTP error message:", error.message);

    return res.status(500).json({
      success: false,
      message: "Server error during resend OTP",
      error: error.message,
    });
  }
};

// LOGIN
const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        success: false,
        message: "Email and password are required",
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    const user = await User.findOne({ email: normalizedEmail });

    if (!user) {
      return res.status(401).json({
        success: false,
        message: "Invalid email or password",
      });
    }

    const isMatch = await user.matchPassword(password);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "Invalid email or password",
      });
    }

    if (!user.isVerified && !user.isEmailVerified) {
      return res.status(403).json({
        success: false,
        message: "Please verify your email before logging in",
        needVerification: true,
        email: user.email,
      });
    }

    const token = generateToken(user._id);

    return res.status(200).json({
      success: true,
      message: "Login successful",
      token,
      user: {
        id: user._id,
        _id: user._id,
        name: user.name,
        email: user.email,
        isVerified: user.isVerified,
        isEmailVerified: user.isEmailVerified,
      },
    });
  } catch (error) {
    console.error("Login error:", error);
    console.error("Login error message:", error.message);

    return res.status(500).json({
      success: false,
      message: "Server error during login",
      error: error.message,
    });
  }
};

module.exports = {
  register,
  verifyOtp,
  resendOtp,
  login,
};