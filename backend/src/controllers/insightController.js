const User = require("../models/User");
const HeartRateRecord = require("../models/HeartRateRecord");
const { generateHeartRateInsight } = require("../services/geminiService");

const getUserHeartRateInsight = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found."
      });
    }

    // Get records from last 7 days, sorted by timestamp descending
    const sevenDaysAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
    const records = await HeartRateRecord.find({
      userId,
      timestamp: { $gte: sevenDaysAgo }
    })
      .sort({ timestamp: -1 })
      .limit(100); // Prevent massive queries

    if (records.length === 0) {
      return res.status(200).json({
        success: true,
        data: {
          insight:
            "Not enough heart-rate data yet. Please record your heart rate a few times to receive a personalised insight."
        }
      });
    }

    const bpmValues = records.map((record) => record.bpmValue);
    const total = bpmValues.reduce((sum, value) => sum + value, 0);

    // Calculate trend (ascending if recent > average)
    const recentRecords = records.slice(0, 5); // Last 5 records
    const recentAvg =
      recentRecords.reduce((sum, r) => sum + r.bpmValue, 0) /
      recentRecords.length;
    const overallAvg = total / bpmValues.length;
    const trend =
      recentAvg > overallAvg ? "ascending" : recentAvg < overallAvg ? "descending" : "stable";

    const stats = {
      averageBpm: Math.round(overallAvg),
      maxBpm: Math.max(...bpmValues),
      minBpm: Math.min(...bpmValues),
      totalRecords: records.length,
      trend
    };

    const insight = await generateHeartRateInsight({
      user,
      stats,
      records
    });

    return res.status(200).json({
      success: true,
      data: {
        stats,
        insight
      }
    });
  } catch (error) {
    console.error("Generate insight error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to generate insight.",
      error: error.message
    });
  }
};

module.exports = {
  getUserHeartRateInsight
};