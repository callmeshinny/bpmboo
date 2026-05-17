const otpStore = new Map();

const generateOtp = () => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

const saveOtp = (email, otp) => {
  const expireMinutes = Number(process.env.OTP_EXPIRE_MINUTES || 5);
  const expiresAt = Date.now() + expireMinutes * 60 * 1000;

  otpStore.set(email.toLowerCase(), {
    otp,
    expiresAt
  });
};

const verifyOtp = (email, inputOtp) => {
  const key = email.toLowerCase();
  const savedData = otpStore.get(key);

  if (!savedData) {
    return {
      success: false,
      message: "OTP not found. Please request a new OTP."
    };
  }

  if (Date.now() > savedData.expiresAt) {
    otpStore.delete(key);

    return {
      success: false,
      message: "OTP has expired. Please request a new OTP."
    };
  }

  if (savedData.otp !== String(inputOtp)) {
    return {
      success: false,
      message: "Invalid OTP. Please try again."
    };
  }

  otpStore.delete(key);

  return {
    success: true,
    message: "OTP verified successfully."
  };
};

module.exports = {
  generateOtp,
  saveOtp,
  verifyOtp
};
