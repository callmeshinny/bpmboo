const User = require("../models/User");
const HeartRateRecord = require("../models/HeartRateRecord");
const { generateHeartRateInsight } = require("../services/geminiService");

const buildEmptyInsightResponse = () => {
  return {
    stats: {
      averageBpm: 0,
      maxBpm: 0,
      minBpm: 0,
      totalRecords: 0,
      trend: "stable",
    },
    insight:
      "Not enough heart-rate data yet. Please record your heart rate a few times to receive a personalised insight.",
  };
};

const buildHeartRateStats = (records) => {
  const bpmValues = records.map((record) => record.bpmValue);
  const total = bpmValues.reduce((sum, value) => sum + value, 0);

  const recentRecords = records.slice(0, 5);
  const recentAvg =
    recentRecords.reduce((sum, record) => sum + record.bpmValue, 0) /
    recentRecords.length;

  const overallAvg = total / bpmValues.length;

  const trend =
    recentAvg > overallAvg
      ? "ascending"
      : recentAvg < overallAvg
        ? "descending"
        : "stable";

  return {
    averageBpm: Math.round(overallAvg),
    maxBpm: Math.max(...bpmValues),
    minBpm: Math.min(...bpmValues),
    totalRecords: records.length,
    trend,
  };
};

const getRecentHeartRateRecords = async (userId) => {
  const sevenDaysAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);

  return await HeartRateRecord.find({
    userId,
    timestamp: { $gte: sevenDaysAgo },
  })
    .sort({ timestamp: -1 })
    .limit(100);
};

const getUserHeartRateInsight = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    const records = await getRecentHeartRateRecords(userId);

    if (records.length === 0) {
      const insightData = buildEmptyInsightResponse();

      return res.status(200).json({
        success: true,
        insightData,
      });
    }

    const stats = buildHeartRateStats(records);

    const insight = await generateHeartRateInsight({
      user,
      stats,
      records,
    });

    return res.status(200).json({
      success: true,
      insightData: {
        stats,
        insight,
      },
    });
  } catch (error) {
    console.error("Generate insight error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to generate insight.",
      error: error.message,
    });
  }
};

const generateHeartRateInsightAPI = async (req, res) => {
  try {
    const { userId } = req.params;

    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found.",
      });
    }

    const records = await getRecentHeartRateRecords(userId);

    if (records.length === 0) {
      const insightData = buildEmptyInsightResponse();

      return res.status(200).json({
        success: true,
        insightData: {
          ...insightData,
          generatedAt: new Date(),
        },
      });
    }

    const stats = buildHeartRateStats(records);

    const insight = await generateHeartRateInsight({
      user,
      stats,
      records,
    });

    return res.status(200).json({
      success: true,
      insightData: {
        stats,
        insight,
        generatedAt: new Date(),
      },
    });
  } catch (error) {
    console.error("Generate insight error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to generate insight.",
      error: error.message,
    });
  }
};

module.exports = {
  getUserHeartRateInsight,
  generateHeartRateInsightAPI,
};