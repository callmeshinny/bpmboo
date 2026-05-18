const express = require("express");

const {
  register,
  login
} = require("../controllers/authController");

const validate = require("../middleware/validateMiddleware");

const {
  validateRegister,
  validateLogin
} = require("../validations/authValidation");

const router = express.Router();

router.post("/register", validate(validateRegister), register);
router.post("/login", validate(validateLogin), login);

module.exports = router;