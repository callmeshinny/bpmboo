const generateBasicInsight = async (healthData) => {
  const averageBpm = Number(healthData.averageBpm || 0);
  const maxBpm = Number(healthData.maxBpm || 0);
  const minBpm = Number(healthData.minBpm || 0);

  let message = "Your heart-rate data looks stable. Keep tracking your readings regularly.";

  if (averageBpm > 100) {
    message = "Your average heart rate seems higher than usual. Consider resting and monitoring your next readings.";
  } else if (averageBpm > 0 && averageBpm < 60) {
    message = "Your average heart rate seems lower than usual. If you feel unwell, consider seeking medical advice.";
  }

  return {
    averageBpm,
    maxBpm,
    minBpm,
    insight: message,
    disclaimer: "This insight is for general wellness tracking only and is not medical advice."
  };
};

module.exports = {
  generateBasicInsight
};
