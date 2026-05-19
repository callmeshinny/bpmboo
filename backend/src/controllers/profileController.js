const User = require("../models/User");

const getProfile = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    return res.status(200).json({
      success: true,
      data: user
    });
  } catch (error) {
    console.error("Get profile error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get profile."
    });
  }
};

const updateProfile = async (req, res) => {
  try {
    const { userId } = req.params;

    const {
      fullName,
      phone,
      dob,
      gender,
      emergencyName,
      emergencyPhone,
      avatarUrl
    } = req.body;

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      {
        fullName,
        phone,
        dob,
        gender,
        emergencyName,
        emergencyPhone,
        avatarUrl
      },
      {
        new: true,
        runValidators: true
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    return res.status(200).json({
      success: true,
      message: "Profile updated successfully.",
      data: updatedUser
    });
  } catch (error) {
    console.error("Update profile error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to update profile."
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
        message: "avatarUrl is required."
      });
    }

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { avatarUrl },
      {
        new: true,
        runValidators: true
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    return res.status(200).json({
      success: true,
      message: "Avatar updated successfully.",
      data: updatedUser
    });
  } catch (error) {
    console.error("Update avatar error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to update avatar."
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
        runValidators: true
      }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    return res.status(200).json({
      success: true,
      message: "Avatar deleted successfully.",
      data: updatedUser
    });
  } catch (error) {
    console.error("Delete avatar error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to delete avatar."
    });
  }
};

module.exports = {
  getProfile,
  updateProfile,
  updateProfileAvatar,
  deleteProfileAvatar
};
