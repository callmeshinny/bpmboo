const express = require("express");
const {
  createHeartRateRecord,
  getHeartRateRecordsByUser,
  deleteHeartRateRecord,
  getHeartRateStats
} = require("../controllers/heartRateController");

const router = express.Router();

router.post("/", createHeartRateRecord);
router.get("/:userId", getHeartRateRecordsByUser);
router.get("/:userId/stats", getHeartRateStats);
router.delete("/:recordId", deleteHeartRateRecord);

module.exports = router;
