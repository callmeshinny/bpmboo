const mongoose = require("mongoose");

const otpCodeSchema = new mongoose.Schema(
  {
    email: {
      type: String,
      required: true,
      lowercase: true,
      trim: true
    },

    otpHash: {
      type: String,
      required: true
    },

    purpose: {
      type: String,
      enum: ["register", "login"],
      default: "login"
    },

    expiresAt: {
      type: Date,
      required: true
    },

    isUsed: {
      type: Boolean,
      default: false
    }
  },
  {
    timestamps: true
  }
);

otpCodeSchema.index({ expiresAt: 1 }, { expireAfterSeconds: 0 });

module.exports = mongoose.model("OtpCode", otpCodeSchema);