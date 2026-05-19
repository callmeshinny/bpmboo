const express = require("express");

const {
  register,
  login,
  getMe,
  changePassword,
  deleteAccount
} = require("../controllers/authController");

const validate = require("../middleware/validateMiddleware");
const authMiddleware = require("../middleware/authMiddleware");

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

/**
 * @swagger
 * /api/auth/me:
 *   get:
 *     summary: Get current authenticated user information
 *     tags: [Auth]
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: Current user information
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: User not found
 */
router.get("/me", authMiddleware, getMe);

/**
 * @swagger
 * /api/auth/change-password:
 *   put:
 *     summary: Change user password
 *     tags: [Auth]
 *     security:
 *       - BearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               currentPassword:
 *                 type: string
 *                 example: OldPassword@123
 *               newPassword:
 *                 type: string
 *                 example: NewPassword@123
 *     responses:
 *       200:
 *         description: Password changed successfully
 *       400:
 *         description: Invalid input
 *       401:
 *         description: Current password incorrect
 */
router.put("/change-password", authMiddleware, changePassword);

/**
 * @swagger
 * /api/auth/delete-account:
 *   delete:
 *     summary: Delete user account and all associated data
 *     tags: [Auth]
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: Account deleted successfully
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: User not found
 */
router.delete("/delete-account", authMiddleware, deleteAccount);

module.exports = router;