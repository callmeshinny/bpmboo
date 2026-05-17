const express = require("express");
const {
  requestOtp,
  verifyOtpCode
} = require("../controllers/otpController");

const router = express.Router();

router.post("/request", requestOtp);
router.post("/verify", verifyOtpCode);

module.exports = router;