const User = require("../models/User");

const buildUserResponse = (user) => {
  return {
    id: user._id,
    _id: user._id,
    name: user.name || user.fullName || "",
    fullName: user.fullName || user.name || "",
    email: user.email,
    phone: user.phone || "",
    dob: user.dob || "",
    gender: user.gender || "",
    avatarUrl: user.avatarUrl || "",
    role: user.role || "user",
    isVerified: user.isVerified,
    isEmailVerified: user.isEmailVerified,
    createdAt: user.createdAt,
    updatedAt: user.updatedAt,
  };
};

const getProfile = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    return res.status(200).json({
      success: true,
      user: buildUserResponse(user),
    });
  } catch (error) {
    console.error("Get profile error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get profile.",
      error: error.message,
    });
  }
};

const updateProfile = async (req, res) => {
  try {
    const { userId } = req.params;

    const {
      fullName,
      name,
      phone,
      email,
      dob,
      gender,
      avatarUrl,
    } = req.body;

    const finalName = fullName || name || "";

    const updateData = {
      fullName: finalName,
      name: finalName,
      phone: phone || "",
      dob: dob || "",
      gender: gender || "",
      avatarUrl: avatarUrl || "",
    };

    if (email) {
      updateData.email = email.trim().toLowerCase();
    }

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      updateData,
      {
        new: true,
        runValidators: true,
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Profile updated successfully.",
      user: buildUserResponse(updatedUser),
    });
  } catch (error) {
    console.error("Update profile error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to update profile.",
      error: error.message,
    });
  }
};

const updateProfileAvatar = async (req, res) => {
  try {
    const { userId } = req.params;
    const { avatarUrl } = req.body;

    if (!avatarUrl) {
      return res.status(400).json({
        success: false,
        message: "avatarUrl is required.",
      });
    }

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { avatarUrl },
      {
        new: true,
        runValidators: true,
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Avatar updated successfully.",
      user: buildUserResponse(updatedUser),
    });
  } catch (error) {
    console.error("Update avatar error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to update avatar.",
      error: error.message,
    });
  }
};

const deleteProfileAvatar = async (req, res) => {
  try {
    const { userId } = req.params;

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { avatarUrl: "" },
      {
        new: true,
        runValidators: true,
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Avatar deleted successfully.",
      user: buildUserResponse(updatedUser),
    });
  } catch (error) {
    console.error("Delete avatar error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to delete avatar.",
      error: error.message,
    });
  }
};

module.exports = {
  getProfile,
  updateProfile,
  updateProfileAvatar,
  deleteProfileAvatar,
};