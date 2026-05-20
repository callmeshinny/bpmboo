const express = require("express");
const {
  register,
  verifyOtp,
  resendOtp,
  login,
  deleteAccount,
} = require("../controllers/authController");

const router = express.Router();

router.post("/register", register);
router.post("/verify-otp", verifyOtp);
router.post("/resend-otp", resendOtp);
router.post("/login", login);
router.delete("/delete-account", deleteAccount);

module.exports = router;