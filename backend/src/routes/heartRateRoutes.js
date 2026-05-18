const express = require("express");
const {
  createHeartRateRecord,
  getHeartRateRecordsByUser,
  deleteHeartRateRecord,
  getHeartRateStats
} = require("../controllers/heartRateController");

const router = express.Router();

/**
 * @swagger
 * /api/heart-rates:
 *   post:
 *     summary: Create a new heart rate record
 *     tags: [Heart Rate]
 *     security:
 *       - BearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               userId:
 *                 type: string
 *               bpmValue:
 *                 type: number
 *                 example: 75
 *               feelingTag:
 *                 type: string
 *                 example: "Resting"
 *               note:
 *                 type: string
 *     responses:
 *       201:
 *         description: Heart rate record created
 */
router.post("/", createHeartRateRecord);

/**
 * @swagger
 * /api/heart-rates/{userId}:
 *   get:
 *     summary: Get all heart rate records for a user
 *     tags: [Heart Rate]
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
 *         description: List of heart rate records
 */
router.get("/:userId", getHeartRateRecordsByUser);

/**
 * @swagger
 * /api/heart-rates/{userId}/stats:
 *   get:
 *     summary: Get heart rate statistics for a user
 *     tags: [Heart Rate]
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
 *         description: Heart rate statistics
 */
router.get("/:userId/stats", getHeartRateStats);

/**
 * @swagger
 * /api/heart-rates/{recordId}:
 *   delete:
 *     summary: Delete a heart rate record
 *     tags: [Heart Rate]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: recordId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Record deleted successfully
 */
router.delete("/:recordId", deleteHeartRateRecord);

module.exports = router;
