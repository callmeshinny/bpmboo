require("dotenv").config();
const mongoose = require("mongoose");

const SOURCE_DB = "test";
const TARGET_DB = "bpmboo";

const collectionsToCopy = [
  "users",
  "otpcodes",
  "heartraterecords"
];

async function migrate() {
  try {
    const uri = process.env.MONGODB_URI;

    if (!uri || !uri.startsWith("mongodb")) {
      throw new Error("Invalid or missing MONGODB_URI");
    }

    await mongoose.connect(uri);

    const client = mongoose.connection.client;

    const sourceDb = client.db(SOURCE_DB);
    const targetDb = client.db(TARGET_DB);

    console.log(`Connected. Copying from "${SOURCE_DB}" to "${TARGET_DB}"...`);

    for (const collectionName of collectionsToCopy) {
      const sourceCollection = sourceDb.collection(collectionName);
      const targetCollection = targetDb.collection(collectionName);

      const documents = await sourceCollection.find({}).toArray();

      if (documents.length === 0) {
        console.log(`Skipped "${collectionName}" because it is empty.`);
        continue;
      }

      let inserted = 0;
      let skipped = 0;

      for (const doc of documents) {
        try {
          await targetCollection.updateOne(
            { _id: doc._id },
            { $setOnInsert: doc },
            { upsert: true }
          );
          inserted++;
        } catch (error) {
          skipped++;
          console.log(`Skipped one document in "${collectionName}": ${error.message}`);
        }
      }

      console.log(
        `Copied "${collectionName}": ${inserted} processed, ${skipped} skipped.`
      );
    }

    console.log("Migration completed successfully.");
    await mongoose.disconnect();
  } catch (error) {
    console.error("Migration failed:", error.message);
    process.exit(1);
  }
}

migrate();