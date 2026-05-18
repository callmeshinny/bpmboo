const { generateGeminiText } = require("../providers/geminiProvider");

const buildHeartRateInsightPrompt = ({ user, stats, records }) => {
  const recentRecords = records.slice(0, 10).map((record) => ({
    bpmValue: record.bpmValue,
    feelingTag: record.feelingTag,
    timestamp: record.timestamp
  }));

  return `
You are BPMBoo Heart Beat, a friendly health tracking assistant.

Important safety rule:
- Do not diagnose disease.
- Do not give medical certainty.
- If data looks concerning, advise the user to rest, monitor, and seek professional medical advice if symptoms persist.

User profile:
- Name: ${user.fullName || "User"}
- Email: ${user.email}

Heart-rate statistics (last 7 days):
- Average BPM: ${stats.averageBpm}
- Max BPM: ${stats.maxBpm}
- Min BPM: ${stats.minBpm}
- Total records: ${stats.totalRecords}
- Trend: ${stats.trend} (recent heart rate is ${stats.trend} compared to average)

Recent records:
${JSON.stringify(recentRecords, null, 2)}

Write a short insight in two parts:
1. English insight, 2-4 sentences. Include the trend observation.
2. Vietnamese insight, 2-4 sentences. Include the trend observation.

Keep the tone warm, simple, and easy to understand. Use casual language.
`;
};

const generateHeartRateInsight = async ({ user, stats, records }) => {
  const prompt = buildHeartRateInsightPrompt({
    user,
    stats,
    records
  });

  return generateGeminiText(prompt);
};

module.exports = {
  generateHeartRateInsight
};