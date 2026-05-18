const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

const validateRegister = (body) => {
  const errors = [];

  const email = body.email ? String(body.email).trim().toLowerCase() : "";
  const password = body.password ? String(body.password) : "";
  const fullName = body.fullName ? String(body.fullName).trim() : "";
  const phone = body.phone ? String(body.phone).trim() : "";

  if (!email) errors.push("Email is required.");
  else if (!isValidEmail(email)) errors.push("Email format is invalid.");

  if (!password) errors.push("Password is required.");
  else if (password.length < 6) errors.push("Password must be at least 6 characters.");

  if (!fullName) errors.push("Full name is required.");
  if (!phone) errors.push("Phone number is required.");

  return {
    isValid: errors.length === 0,
    errors
  };
};

const validateLogin = (body) => {
  const errors = [];

  const email = body.email ? String(body.email).trim().toLowerCase() : "";
  const password = body.password ? String(body.password) : "";

  if (!email) errors.push("Email is required.");
  else if (!isValidEmail(email)) errors.push("Email format is invalid.");

  if (!password) errors.push("Password is required.");

  return {
    isValid: errors.length === 0,
    errors
  };
};

module.exports = {
  validateRegister,
  validateLogin
};