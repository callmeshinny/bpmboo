const express = require("express");
const {
  getProfile,
  updateProfile
} = require("../controllers/profileController");

const router = express.Router();

/**
 * @swagger
 * /api/profile/{userId}:
 *   get:
 *     summary: Get user profile
 *     tags: [Profile]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: User profile data
 */
router.get("/:userId", getProfile);

/**
 * @swagger
 * /api/profile/{userId}:
 *   put:
 *     summary: Update user profile
 *     tags: [Profile]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               fullName:
 *                 type: string
 *               phone:
 *                 type: string
 *               dob:
 *                 type: string
 *               gender:
 *                 type: string
 *               emergencyName:
 *                 type: string
 *               emergencyPhone:
 *                 type: string
 *               avatarUrl:
 *                 type: string
 *     responses:
 *       200:
 *         description: Profile updated successfully
 */
router.put("/:userId", updateProfile);

module.exports = router;
