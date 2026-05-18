const express = require("express");

const {
  register,
  login
} = require("../controllers/authController");

const {
  requestOtp,
  verifyOtpCode
} = require("../controllers/otpController");

const router = express.Router();

router.post("/register", register);
router.post("/login", login);

router.post("/login/request-otp", requestOtp);
router.post("/login/verify-otp", verifyOtpCode);

module.exports = router;