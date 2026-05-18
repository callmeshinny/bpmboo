const User = require("../models/User");

const uploadAvatar = async (req, res) => {
  try {
    const { userId } = req.params;
    const { avatarBase64 } = req.body;

    if (!avatarBase64) {
      return res.status(400).json({
        success: false,
        message: "Avatar base64 data is required"
      });
    }

    const user = await User.findByIdAndUpdate(
      userId,
      { avatarUrl: avatarBase64 },
      { new: true }
    );

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found"
      });
    }

    res.status(200).json({
      success: true,
      data: {
        avatarUrl: user.avatarUrl,
        message: "Avatar uploaded successfully"
      }
    });
  } catch (error) {
    console.error("Error uploading avatar:", error);
    res.status(500).json({
      success: false,
      message: "Failed to upload avatar",
      error: error.message
    });
  }
};

const getAvatar = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId).select("avatarUrl");

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found"
      });
    }

    res.status(200).json({
      success: true,
      data: {
        avatarUrl: user.avatarUrl || null
      }
    });
  } catch (error) {
    console.error("Error fetching avatar:", error);
    res.status(500).json({
      success: false,
      message: "Failed to fetch avatar",
      error: error.message
    });
  }
};

module.exports = {
  uploadAvatar,
  getAvatar
};
