const swaggerJsdoc = require("swagger-jsdoc");

const options = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "BPMBoo Backend API",
      version: "1.0.0",
      description: "Heart Rate Tracking & AI Insights API with OTP Authentication"
    },
    servers: [
      {
        url: "http://localhost:5001",
        description: "Local development server"
      },
      {
        url: "https://bpmboo-backend.onrender.com",
        description: "Production server (Render)"
      }
    ],
    components: {
      securitySchemes: {
        BearerAuth: {
          type: "http",
          scheme: "bearer",
          bearerFormat: "JWT"
        }
      },
      schemas: {
        User: {
          type: "object",
          properties: {
            _id: { type: "string" },
            email: { type: "string" },
            fullName: { type: "string" },
            phone: { type: "string" },
            dob: { type: "string" },
            gender: { type: "string", enum: ["male", "female", "other"] },
            emergencyName: { type: "string" },
            emergencyPhone: { type: "string" },
            avatarUrl: { type: "string" },
            isEmailVerified: { type: "boolean" }
          }
        },
        HeartRateRecord: {
          type: "object",
          properties: {
            _id: { type: "string" },
            userId: { type: "string" },
            bpmValue: { type: "number" },
            feelingTag: { type: "string" },
            note: { type: "string" },
            timestamp: { type: "string", format: "date-time" }
          }
        },
        Stats: {
          type: "object",
          properties: {
            averageBpm: { type: "number" },
            maxBpm: { type: "number" },
            minBpm: { type: "number" },
            totalRecords: { type: "number" },
            trend: { type: "string" }
          }
        }
      }
    }
  },
  apis: [
    "./src/routes/authRoutes.js",
    "./src/routes/otpRoutes.js",
    "./src/routes/heartRateRoutes.js",
    "./src/routes/profileRoutes.js",
    "./src/routes/insightRoutes.js"
  ]
};

const swaggerSpec = swaggerJsdoc(options);

module.exports = swaggerSpec;
