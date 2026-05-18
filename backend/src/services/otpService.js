const bcrypt = require("bcryptjs");

const generateOtp = () => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

const hashOtp = async (otp) => {
  return bcrypt.hash(otp, 10);
};

const compareOtp = async (plainOtp, otpHash) => {
  return bcrypt.compare(plainOtp, otpHash);
};

const getOtpExpiryDate = () => {
  const minutes = Number(process.env.OTP_EXPIRE_MINUTES || 5);
  return new Date(Date.now() + minutes * 60 * 1000);
};

module.exports = {
  generateOtp,
  hashOtp,
  compareOtp,
  getOtpExpiryDate
};