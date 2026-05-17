const { generateBasicInsight } = require("../services/geminiService");

const createInsight = async (req, res) => {
  try {
    const {
      averageBpm,
      maxBpm,
      minBpm,
      weeklyTrend
    } = req.body;

    const insight = await generateBasicInsight({
      averageBpm,
      maxBpm,
      minBpm,
      weeklyTrend
    });

    return res.status(200).json({
      success: true,
      message: "Insight generated successfully.",
      data: insight
    });
  } catch (error) {
    console.error("Create insight error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to generate insight."
    });
  }
};

module.exports = {
  createInsight
};
