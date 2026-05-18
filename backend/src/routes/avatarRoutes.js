const express = require("express");
const { uploadAvatar, getAvatar } = require("../controllers/avatarController");

const router = express.Router();

/**
 * @swagger
 * /api/avatar/{userId}:
 *   post:
 *     summary: Upload user avatar
 *     tags: [Avatar]
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
 *               avatarBase64:
 *                 type: string
 *                 description: Base64 encoded image
 *     responses:
 *       200:
 *         description: Avatar uploaded successfully
 */
router.post("/:userId", uploadAvatar);

/**
 * @swagger
 * /api/avatar/{userId}:
 *   get:
 *     summary: Get user avatar
 *     tags: [Avatar]
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Avatar URL
 */
router.get("/:userId", getAvatar);

module.exports = router;
