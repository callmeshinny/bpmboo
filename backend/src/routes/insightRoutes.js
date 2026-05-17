const express = require("express");
const { createInsight } = require("../controllers/insightController");

const router = express.Router();

router.post("/weekly", createInsight);

module.exports = router;
