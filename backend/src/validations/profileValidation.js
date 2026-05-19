const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

const validateProfileUpdate = (body) => {
  const errors = [];

  const fullName = body.fullName ? String(body.fullName).trim() : "";
  const phone = body.phone ? String(body.phone).trim() : "";
  const email = body.email ? String(body.email).trim().toLowerCase() : "";
  const dob = body.dob ? String(body.dob).trim() : "";
  const gender = body.gender ? String(body.gender).trim() : "";

  if (!fullName) {
    errors.push("Full name is required.");
  }

  if (!phone) {
    errors.push("Phone number is required.");
  }

  if (!email) {
    errors.push("Email is required.");
  } else if (!isValidEmail(email)) {
    errors.push("Email format is invalid.");
  }

  if (!dob) {
    errors.push("Date of birth is required.");
  }

  if (!gender) {
    errors.push("Gender is required.");
  } else if (!["Male", "Female", "Other"].includes(gender)) {
    errors.push("Gender must be Male, Female, or Other.");
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

module.exports = {
  validateProfileUpdate
};