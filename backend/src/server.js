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

if (process.env.MONGODB_URI && process.env.MONGODB_URI.startsWith("mongodb")) {
  connectDB();
} else {
  console.log("MongoDB URI is missing or invalid. Running without database for now.");
}

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

app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: "API route not found."
  });
});

const PORT = process.env.PORT || 5001;

app.listen(PORT, () => {
  console.log(`BPMBoo backend is running on port ${PORT}`);
});
