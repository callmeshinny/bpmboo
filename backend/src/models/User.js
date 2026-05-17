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
    fullName: {
      type: String,
      default: ""
    },
    phone: {
      type: String,
      default: ""
    },
    dob: {
      type: String,
      default: ""
    },
    gender: {
      type: String,
      default: ""
    },
    emergencyName: {
      type: String,
      default: ""
    },
    emergencyPhone: {
      type: String,
      default: ""
    },
    avatarUrl: {
      type: String,
      default: ""
    }
  },
  {
    timestamps: true
  }
);

module.exports = mongoose.model("User", userSchema);
