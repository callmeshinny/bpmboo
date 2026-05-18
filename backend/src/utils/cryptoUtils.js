const crypto = require("crypto");

const ALGORITHM = "aes-256-gcm";

const getEncryptionKey = () => {
  const key = process.env.ENCRYPTION_KEY;

  if (!key) {
    throw new Error("Missing ENCRYPTION_KEY in .env");
  }

  const buffer = Buffer.from(key, "base64");

  if (buffer.length !== 32) {
    throw new Error("ENCRYPTION_KEY must be 32 bytes in base64 format");
  }

  return buffer;
};

const encryptText = (plainText) => {
  if (plainText === undefined || plainText === null || plainText === "") {
    return "";
  }

  const iv = crypto.randomBytes(12);
  const key = getEncryptionKey();

  const cipher = crypto.createCipheriv(ALGORITHM, key, iv);

  const encrypted = Buffer.concat([
    cipher.update(String(plainText), "utf8"),
    cipher.final()
  ]);

  const authTag = cipher.getAuthTag();

  return [
    iv.toString("base64"),
    authTag.toString("base64"),
    encrypted.toString("base64")
  ].join(":");
};

const decryptText = (encryptedText) => {
  if (!encryptedText) {
    return "";
  }

  const [ivBase64, authTagBase64, encryptedBase64] = encryptedText.split(":");

  if (!ivBase64 || !authTagBase64 || !encryptedBase64) {
    return "";
  }

  const key = getEncryptionKey();
  const iv = Buffer.from(ivBase64, "base64");
  const authTag = Buffer.from(authTagBase64, "base64");
  const encrypted = Buffer.from(encryptedBase64, "base64");

  const decipher = crypto.createDecipheriv(ALGORITHM, key, iv);
  decipher.setAuthTag(authTag);

  const decrypted = Buffer.concat([
    decipher.update(encrypted),
    decipher.final()
  ]);

  return decrypted.toString("utf8");
};

module.exports = {
  encryptText,
  decryptText
};
