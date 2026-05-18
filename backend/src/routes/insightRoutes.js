const express = require("express");

const {
  getUserHeartRateInsight
} = require("../controllers/insightController");

const router = express.Router();

router.get("/summary/:userId", getUserHeartRateInsight);

module.exports = router;