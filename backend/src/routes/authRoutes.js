const express = require("express");
const {
  requestOtp,
  verifyOtpCode
} = require("../controllers/otpController");

const router = express.Router();

router.post("/login/request-otp", requestOtp);
router.post("/login/verify-otp", verifyOtpCode);

module.exports = router;
