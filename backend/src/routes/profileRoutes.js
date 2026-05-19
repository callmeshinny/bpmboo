const express = require("express");
const {
  getProfile,
  updateProfile,
  updateProfileAvatar,
  deleteProfileAvatar
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

/**
 * @swagger
 * /api/profile/{userId}/avatar:
 *   patch:
 *     summary: Update user profile avatar
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
 *               avatarUrl:
 *                 type: string
 *                 example: "https://example.com/avatar.jpg"
 *     responses:
 *       200:
 *         description: Avatar updated successfully
 *       404:
 *         description: User not found
 */
router.patch("/:userId/avatar", updateProfileAvatar);

/**
 * @swagger
 * /api/profile/{userId}/avatar:
 *   delete:
 *     summary: Delete user profile avatar
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
 *         description: Avatar deleted successfully
 *       404:
 *         description: User not found
 */
router.delete("/:userId/avatar", deleteProfileAvatar);

module.exports = router;
