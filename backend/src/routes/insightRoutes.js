const express = require("express");

const {
  getUserHeartRateInsight
} = require("../controllers/insightController");

const router = express.Router();

/**
 * @swagger
 * /api/insight/summary/{userId}:
 *   get:
 *     summary: Get AI-generated heart rate insight
 *     tags: [Insight]
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
 *         description: Heart rate insight with stats and trend analysis
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                 data:
 *                   type: object
 *                   properties:
 *                     stats:
 *                       $ref: '#/components/schemas/Stats'
 *                     insight:
 *                       type: string
 *       404:
 *         description: User not found
 */
router.get("/summary/:userId", getUserHeartRateInsight);

module.exports = router;