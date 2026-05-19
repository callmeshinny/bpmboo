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

const getLatestHeartRateRecord = async (req, res) => {
  try {
    const { userId } = req.params;

    const record = await HeartRateRecord.findOne({ userId })
      .sort({ timestamp: -1 });

    if (!record) {
      return res.status(404).json({
        success: false,
        message: "No heart-rate records found."
      });
    }

    return res.status(200).json({
      success: true,
      data: record
    });
  } catch (error) {
    console.error("Get latest heart-rate record error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get latest heart-rate record."
    });
  }
};

const getTakeHeartRateRecords = async (req, res) => {
  try {
    const { userId } = req.params;
    const limit = Math.min(parseInt(req.query.limit) || 10, 100);

    const records = await HeartRateRecord.find({ userId })
      .sort({ timestamp: -1 })
      .limit(limit);

    return res.status(200).json({
      success: true,
      count: records.length,
      data: records
    });
  } catch (error) {
    console.error("Get take heart-rate records error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get heart-rate records."
    });
  }
};

const updateHeartRateRecord = async (req, res) => {
  try {
    const { recordId } = req.params;
    const { bpmValue, feelingTag, note } = req.body;

    const updatedRecord = await HeartRateRecord.findByIdAndUpdate(
      recordId,
      {
        ...(bpmValue !== undefined && { bpmValue }),
        ...(feelingTag !== undefined && { feelingTag }),
        ...(note !== undefined && { note })
      },
      {
        new: true,
        runValidators: true
      }
    );

    if (!updatedRecord) {
      return res.status(404).json({
        success: false,
        message: "Heart-rate record not found."
      });
    }

    return res.status(200).json({
      success: true,
      message: "Heart-rate record updated successfully.",
      data: updatedRecord
    });
  } catch (error) {
    console.error("Update heart-rate record error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to update heart-rate record."
    });
  }
};

const getHeartRateRange = async (req, res) => {
  try {
    const { userId } = req.params;
    const { from, to } = req.query;

    const query = { userId };

    if (from || to) {
      query.timestamp = {};

      if (from) {
        const fromDate = new Date(from);
        if (isNaN(fromDate.getTime())) {
          return res.status(400).json({
            success: false,
            message: "Invalid 'from' date format. Use YYYY-MM-DD."
          });
        }
        query.timestamp.$gte = fromDate;
      }

      if (to) {
        const toDate = new Date(to);
        if (isNaN(toDate.getTime())) {
          return res.status(400).json({
            success: false,
            message: "Invalid 'to' date format. Use YYYY-MM-DD."
          });
        }
        // Set to end of day
        toDate.setHours(23, 59, 59, 999);
        query.timestamp.$lte = toDate;
      }
    }

    const records = await HeartRateRecord.find(query)
      .sort({ timestamp: -1 });

    return res.status(200).json({
      success: true,
      count: records.length,
      data: records
    });
  } catch (error) {
    console.error("Get heart-rate range error:", error.message);

    return res.status(500).json({
      success: false,
      message: "Failed to get heart-rate records by date range."
    });
  }
};

module.exports = {
  createHeartRateRecord,
  getHeartRateRecordsByUser,
  getLatestHeartRateRecord,
  getTakeHeartRateRecords,
  updateHeartRateRecord,
  deleteHeartRateRecord,
  getHeartRateStats,
  getHeartRateRange
};
