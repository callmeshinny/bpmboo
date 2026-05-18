const validate = (validator) => {
  return (req, res, next) => {
    const result = validator(req.body);

    if (!result.isValid) {
      return res.status(400).json({
        success: false,
        message: "Validation failed.",
        errors: result.errors
      });
    }

    next();
  };
};

module.exports = validate;