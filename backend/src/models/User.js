const mongoose = require("mongoose");

const userSchema = new mongoose.Schema(
  {
    email: {
      type: String,
      required: true,
      unique: true,
      lowercase: true,
      trim: true
    },

    passwordHash: {
      type: String,
      required: true
    },

    fullName: {
      type: String,
      default: ""
    },

    phoneEncrypted: {
      type: String,
      default: ""
    },

    dobEncrypted: {
      type: String,
      default: ""
    },

    genderEncrypted: {
      type: String,
      default: ""
    },

    emergencyNameEncrypted: {
      type: String,
      default: ""
    },

    emergencyPhoneEncrypted: {
      type: String,
      default: ""
    },

    avatarUrl: {
      type: String,
      default: ""
    },

    role: {
      type: String,
      enum: ["user", "admin"],
      default: "user"
    },

    isEmailVerified: {
      type: Boolean,
      default: false
    }
  },
  {
    timestamps: true
  }
);

module.exports = mongoose.model("User", userSchema);
