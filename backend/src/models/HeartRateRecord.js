const mongoose = require("mongoose");

const heartRateRecordSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true
    },
    bpmValue: {
      type: Number,
      required: true
    },
    timestamp: {
      type: Date,
      default: Date.now
    },
    feelingTag: {
      type: String,
      default: "Resting"
    },
    note: {
      type: String,
      default: ""
    }
  },
  {
    timestamps: true
  }
);

module.exports = mongoose.model("HeartRateRecord", heartRateRecordSchema);
