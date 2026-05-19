const nodemailer = require("nodemailer");

let transporter = null;

const createTransporter = () => {
  if (transporter) {
    return transporter;
  }

  if (!process.env.EMAIL_USER || !process.env.EMAIL_PASS) {
    throw new Error("EMAIL_USER and EMAIL_PASS are required");
  }

  transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
      user: process.env.EMAIL_USER,
      pass: process.env.EMAIL_PASS
    }
  });

  return transporter;
};

module.exports = {
  createTransporter
};