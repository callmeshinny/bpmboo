const express = require("express");

const {
  register,
  login
} = require("../controllers/authController");

const validate = require("../middleware/validateMiddleware");

const {
  validateRegister,
  validateLogin
} = require("../validations/authValidation");

const router = express.Router();

/**
 * @swagger
 * /api/auth/register:
 *   post:
 *     summary: Register a new user
 *     tags: [Auth]
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
 *               password:
 *                 type: string
 *                 example: MyPassword@123
 *               fullName:
 *                 type: string
 *                 example: John Doe
 *               phone:
 *                 type: string
 *                 example: "0123456789"
 *               dob:
 *                 type: string
 *                 example: "2000-01-15"
 *               gender:
 *                 type: string
 *                 enum: [male, female, other]
 *               emergencyName:
 *                 type: string
 *               emergencyPhone:
 *                 type: string
 *     responses:
 *       201:
 *         description: User registered successfully
 *       400:
 *         description: Validation error
 */
router.post("/register", validate(validateRegister), register);

/**
 * @swagger
 * /api/auth/login:
 *   post:
 *     summary: Login user with email and password
 *     tags: [Auth]
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
 *               password:
 *                 type: string
 *                 example: MyPassword@123
 *     responses:
 *       200:
 *         description: Login successful
 *       401:
 *         description: Invalid credentials
 */
router.post("/login", validate(validateLogin), login);

module.exports = router;