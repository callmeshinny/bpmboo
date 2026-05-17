const HeartRateRecord = require("../models/HeartRateRecord");

const createHeartRateRecord = async (req, res) => {
  try {
    const {
      userId,
      bpmValue,
      timestamp,
      feelingTag,
      note
    } = req.body;

    if (!userId || !bpmValue) {
      return res.status(400).json({
        success: false,
        message: "userId and bpmValue are required."
      });
    }

    const record = await HeartRateRecord.create({
      userId,
      bpmValue,
      timestamp: timestamp || Date.now(),
      feelingTag,
      note
    });

    return res.status(201).json({
      success: true,
      message: "Heart-rate record saved successfully.",
      data: record
    });
  } catch (error) {
    console.error("Create heart-rate record error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to save heart-rate record."
    });
  }
};

const getHeartRateRecordsByUser = async (req, res) => {
  try {
    const { userId } = req.params;

    const records = await HeartRateRecord.find({ userId })
      .sort({ timestamp: -1 });

    return res.status(200).json({
      success: true,
      count: records.length,
      data: records
    });
  } catch (error) {
    console.error("Get heart-rate records error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get heart-rate records."
    });
  }
};

const deleteHeartRateRecord = async (req, res) => {
  try {
    const { recordId } = req.params;

    const deletedRecord = await HeartRateRecord.findByIdAndDelete(recordId);

    if (!deletedRecord) {
      return res.status(404).json({
        success: false,
        message: "Heart-rate record not found."
      });
    }

    return res.status(200).json({
      success: true,
      message: "Heart-rate record deleted successfully."
    });
  } catch (error) {
    console.error("Delete heart-rate record error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to delete heart-rate record."
    });
  }
};

const getHeartRateStats = async (req, res) => {
  try {
    const { userId } = req.params;

    const records = await HeartRateRecord.find({ userId });

    if (records.length === 0) {
      return res.status(200).json({
        success: true,
        data: {
          averageBpm: 0,
          maxBpm: 0,
          minBpm: 0,
          totalRecords: 0
        }
      });
    }

    const bpmValues = records.map((record) => record.bpmValue);
    const total = bpmValues.reduce((sum, value) => sum + value, 0);

    return res.status(200).json({
      success: true,
      data: {
        averageBpm: Math.round(total / bpmValues.length),
        maxBpm: Math.max(...bpmValues),
        minBpm: Math.min(...bpmValues),
        totalRecords: records.length
      }
    });
  } catch (error) {
    console.error("Get heart-rate stats error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get heart-rate stats."
    });
  }
};

module.exports = {
  createHeartRateRecord,
  getHeartRateRecordsByUser,
  deleteHeartRateRecord,
  getHeartRateStats
};
