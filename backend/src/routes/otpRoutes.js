const express = require("express");

const {
  requestOtp,
  resendOtp,
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
 *               purpose:
 *                 type: string
 *                 enum: [register, login]
 *                 example: register
 *                 description: Purpose for requesting OTP - "register" for new account verification, "login" for login verification
 *     responses:
 *       200:
 *         description: OTP sent successfully
 *       400:
 *         description: Invalid email or purpose
 *       404:
 *         description: User not found
 */
router.post("/request", validate(validateOtpRequest), requestOtp);

/**
 * @swagger
 * /api/otp/resend:
 *   post:
 *     summary: Resend OTP code to email
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
 *         description: OTP resent successfully
 *       400:
 *         description: Invalid email
 */
router.post("/resend", validate(validateOtpRequest), resendOtp);

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
 *               otp:
 *                 type: string
 *                 example: "123456"
 *                 description: 6-digit OTP code
 *               purpose:
 *                 type: string
 *                 enum: [register, login]
 *                 example: register
 *                 description: Purpose for verifying OTP - must match the purpose used in request
 *     responses:
 *       200:
 *         description: OTP verified successfully
 *       400:
 *         description: Invalid OTP or email
 *       404:
 *         description: OTP code not found or expired
 */
router.post("/verify", validate(validateOtpVerify), verifyOtpCode);

module.exports = router;