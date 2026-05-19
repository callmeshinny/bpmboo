const express = require("express");
const {
  createHeartRateRecord,
  getHeartRateRecordsByUser,
  getLatestHeartRateRecord,
  getTakeHeartRateRecords,
  updateHeartRateRecord,
  deleteHeartRateRecord,
  getHeartRateStats,
  getHeartRateRange
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
 * /api/heart-rates/{userId}/latest:
 *   get:
 *     summary: Get the latest heart rate record for a user
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
 *         description: Latest heart rate record
 *       404:
 *         description: No records found
 */
router.get("/:userId/latest", getLatestHeartRateRecord);

/**
 * @swagger
 * /api/heart-rates/{userId}/take:
 *   get:
 *     summary: Get last N heart rate records for a user
 *     tags: [Heart Rate]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *       - in: query
 *         name: limit
 *         required: false
 *         schema:
 *           type: integer
 *           default: 10
 *         description: Maximum number of records to return (max 100)
 *     responses:
 *       200:
 *         description: List of heart rate records
 */
router.get("/:userId/take", getTakeHeartRateRecords);

/**
 * @swagger
 * /api/heart-rates/{userId}/range:
 *   get:
 *     summary: Get heart rate records within a date range
 *     tags: [Heart Rate]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *       - in: query
 *         name: from
 *         required: false
 *         schema:
 *           type: string
 *           format: date
 *         description: Start date (YYYY-MM-DD)
 *       - in: query
 *         name: to
 *         required: false
 *         schema:
 *           type: string
 *           format: date
 *         description: End date (YYYY-MM-DD)
 *     responses:
 *       200:
 *         description: List of heart rate records in date range
 *       400:
 *         description: Invalid date format
 */
router.get("/:userId/range", getHeartRateRange);

/**
 * @swagger
 * /api/heart-rates/{recordId}:
 *   put:
 *     summary: Update a heart rate record
 *     tags: [Heart Rate]
 *     security:
 *       - BearerAuth: []
 *     parameters:
 *       - in: path
 *         name: recordId
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
 *               bpmValue:
 *                 type: number
 *                 example: 88
 *               feelingTag:
 *                 type: string
 *                 example: "After exercise"
 *               note:
 *                 type: string
 *     responses:
 *       200:
 *         description: Record updated successfully
 *       404:
 *         description: Record not found
 */
router.put("/:recordId", updateHeartRateRecord);

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
