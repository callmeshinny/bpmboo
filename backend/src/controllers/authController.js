const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

const User = require("../models/User");
const HeartRateRecord = require("../models/HeartRateRecord");
const { encryptText, decryptText } = require("../utils/cryptoUtils");

const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

const createToken = (user) => {
  return jwt.sign(
    {
      userId: user._id,
      email: user.email,
      role: user.role
    },
    process.env.JWT_SECRET,
    {
      expiresIn: "7d"
    }
  );
};

const formatUserResponse = (user) => {
  return {
    _id: user._id,
    email: user.email,
    fullName: user.fullName,
    phone: decryptText(user.phoneEncrypted),
    dob: decryptText(user.dobEncrypted),
    gender: decryptText(user.genderEncrypted),
    emergencyName: decryptText(user.emergencyNameEncrypted),
    emergencyPhone: decryptText(user.emergencyPhoneEncrypted),
    avatarUrl: user.avatarUrl,
    role: user.role,
    isEmailVerified: user.isEmailVerified,
    createdAt: user.createdAt,
    updatedAt: user.updatedAt
  };
};

const register = async (req, res) => {
  try {
    const {
      email,
      password,
      fullName,
      phone,
      dob,
      gender,
      emergencyName,
      emergencyPhone,
      avatarUrl
    } = req.body;

    if (!email || !isValidEmail(email)) {
      return res.status(400).json({
        success: false,
        message: "Valid email is required."
      });
    }

    if (!password || password.length < 6) {
      return res.status(400).json({
        success: false,
        message: "Password must be at least 6 characters."
      });
    }

    const existingUser = await User.findOne({
      email: email.toLowerCase()
    });

    if (existingUser) {
      return res.status(409).json({
        success: false,
        message: "Email already exists."
      });
    }

    const passwordHash = await bcrypt.hash(password, 12);

    const user = await User.create({
      email: email.toLowerCase(),
      passwordHash,
      fullName: fullName || "",
      phoneEncrypted: encryptText(phone),
      dobEncrypted: encryptText(dob),
      genderEncrypted: encryptText(gender),
      emergencyNameEncrypted: encryptText(emergencyName),
      emergencyPhoneEncrypted: encryptText(emergencyPhone),
      avatarUrl: avatarUrl || ""
    });

    const token = createToken(user);

    return res.status(201).json({
      success: true,
      message: "Account created successfully.",
      token,
      user: formatUserResponse(user)
    });
  } catch (error) {
    console.error("Register error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to register account.",
      error: error.message
    });
  }
};

const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !isValidEmail(email)) {
      return res.status(400).json({
        success: false,
        message: "Valid email is required."
      });
    }

    if (!password) {
      return res.status(400).json({
        success: false,
        message: "Password is required."
      });
    }

    const user = await User.findOne({
      email: email.toLowerCase()
    });

    if (!user) {
      return res.status(401).json({
        success: false,
        message: "Invalid email or password."
      });
    }

    const isMatch = await bcrypt.compare(password, user.passwordHash);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "Invalid email or password."
      });
    }

    const token = createToken(user);

    return res.status(200).json({
      success: true,
      message: "Login successful.",
      token,
      user: formatUserResponse(user)
    });
  } catch (error) {
    console.error("Login error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to login."
    });
  }
};

const getMe = async (req, res) => {
  try {
    const userId = req.user.userId;

    const user = await User.findById(userId).select("-passwordHash");

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    return res.status(200).json({
      success: true,
      data: formatUserResponse(user)
    });
  } catch (error) {
    console.error("Get me error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get user information."
    });
  }
};

const changePassword = async (req, res) => {
  try {
    const userId = req.user.userId;
    const { currentPassword, newPassword } = req.body;

    if (!currentPassword || !newPassword) {
      return res.status(400).json({
        success: false,
        message: "Current password and new password are required."
      });
    }

    if (newPassword.length < 6) {
      return res.status(400).json({
        success: false,
        message: "New password must be at least 6 characters."
      });
    }

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    const isMatch = await bcrypt.compare(currentPassword, user.passwordHash);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "Current password is incorrect."
      });
    }

    const passwordHash = await bcrypt.hash(newPassword, 12);
    user.passwordHash = passwordHash;
    await user.save();

    return res.status(200).json({
      success: true,
      message: "Password changed successfully."
    });
  } catch (error) {
    console.error("Change password error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to change password."
    });
  }
};

const deleteAccount = async (req, res) => {
  try {
    const userId = req.user.userId;

    const user = await User.findByIdAndDelete(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    // Also delete all heart rate records
    await HeartRateRecord.deleteMany({ userId });

    return res.status(200).json({
      success: true,
      message: "Account deleted successfully."
    });
  } catch (error) {
    console.error("Delete account error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to delete account."
    });
  }
};

module.exports = {
  register,
  login,
  getMe,
  changePassword,
  deleteAccount,
  formatUserResponse
};
