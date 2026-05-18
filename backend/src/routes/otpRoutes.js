const express = require("express");

const {
  requestOtp,
  verifyOtpCode
} = require("../controllers/otpController");

const validate = require("../middleware/validateMiddleware");

const {
  validateOtpRequest,
  validateOtpVerify
} = require("../validations/otpValidation");

const router = express.Router();

router.post("/request", validate(validateOtpRequest), requestOtp);
router.post("/verify", validate(validateOtpVerify), verifyOtpCode);

module.exports = router;