require("dotenv").config();
const mongoose = require("mongoose");

const SOURCE_DB = "test";

const collectionsToDelete = [
  "users",
  "otpcodes",
  "heartraterecords"
];

async function deleteTestData() {
  try {
    await mongoose.connect(process.env.MONGODB_URI);

    const client = mongoose.connection.client;
    const sourceDb = client.db(SOURCE_DB);

    for (const collectionName of collectionsToDelete) {
      const result = await sourceDb.collection(collectionName).deleteMany({});
      console.log(`Deleted ${result.deletedCount} documents from test.${collectionName}`);
    }

    console.log("Test data deleted successfully.");
    await mongoose.disconnect();
  } catch (error) {
    console.error("Delete failed:", error.message);
    process.exit(1);
  }
}

deleteTestData();