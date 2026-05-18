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

/**
 * @swagger
 * /api/otp/request:
 *   post:
 *     summary: Request OTP code for email verification
 *     tags: [OTP]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               email:
 *                 type: string
 *                 example: user@example.com
 *     responses:
 *       200:
 *         description: OTP sent successfully
 *       400:
 *         description: Invalid email
 */
router.post("/request", validate(validateOtpRequest), requestOtp);

/**
 * @swagger
 * /api/otp/verify:
 *   post:
 *     summary: Verify OTP code
 *     tags: [OTP]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               email:
 *                 type: string
 *                 example: user@example.com
 *               otpCode:
 *                 type: string
 *                 example: "123456"
 *     responses:
 *       200:
 *         description: OTP verified successfully
 *       400:
 *         description: Invalid OTP
 */
router.post("/verify", validate(validateOtpVerify), verifyOtpCode);

module.exports = router;