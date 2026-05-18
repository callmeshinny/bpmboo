const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

const validateOtpRequest = (body) => {
  const errors = [];

  const email = body.email ? String(body.email).trim().toLowerCase() : "";
  const purpose = body.purpose || "login";

  if (!email) errors.push("Email is required.");
  else if (!isValidEmail(email)) errors.push("Email format is invalid.");

  if (!["register", "login"].includes(purpose)) {
    errors.push("Purpose must be either register or login.");
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

const validateOtpVerify = (body) => {
  const errors = [];

  const email = body.email ? String(body.email).trim().toLowerCase() : "";
  const otp = body.otp ? String(body.otp).trim() : "";
  const purpose = body.purpose || "login";

  if (!email) errors.push("Email is required.");
  else if (!isValidEmail(email)) errors.push("Email format is invalid.");

  if (!otp) errors.push("OTP is required.");
  else if (!/^\d{6}$/.test(otp)) errors.push("OTP must be 6 digits.");

  if (!["register", "login"].includes(purpose)) {
    errors.push("Purpose must be either register or login.");
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

module.exports = {
  validateOtpRequest,
  validateOtpVerify
};