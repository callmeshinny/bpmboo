require("dotenv").config();

const express = require("express");
const cors = require("cors");

const connectDB = require("./config/db");

const otpRoutes = require("./routes/otpRoutes");
const authRoutes = require("./routes/authRoutes");
const insightRoutes = require("./routes/insightRoutes");
const profileRoutes = require("./routes/profileRoutes");
const heartRateRoutes = require("./routes/heartRateRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.status(200).json({
    success: true,
    message: "BPMBoo backend is running."
  });
});

app.get("/api/health", (req, res) => {
  res.status(200).json({
    success: true,
    status: "OK",
    service: "BPMBoo Backend"
  });
});

app.use("/api/otp", otpRoutes);
app.use("/api/auth", authRoutes);
app.use("/api/insight", insightRoutes);
app.use("/api/profile", profileRoutes);
app.use("/api/heart-rates", heartRateRoutes);

console.log("All routes mounted successfully");

app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: "API route not found."
  });
});

const PORT = process.env.PORT || 5001;

const startServer = async () => {
  try {
    await connectDB();

    app.listen(PORT, "0.0.0.0", () => {
      console.log(`BPMBoo backend is running on port ${PORT}`);
    });
  } catch (error) {
    console.error("Failed to start server:", error.message);
    process.exit(1);
  }
};

startServer();