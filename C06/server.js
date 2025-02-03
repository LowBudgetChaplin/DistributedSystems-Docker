const express = require('express');
const mysql = require('mysql2/promise');
const { MongoClient } = require('mongodb');

const app = express();
const port = process.env.PORT || 3101;
app.use(express.static('public'));

const mysqlConfig = {
  host: process.env.MYSQL_HOST || 'mysql',
  user: process.env.MYSQL_USER || 'bmp_user',
  password: process.env.MYSQL_PASSWORD || 'bmp_pass',
  database: process.env.MYSQL_DATABASE || 'bmp_db'
};

const mongoUri = process.env.MONGO_URI || 'mongodb://mongo:27017';
const mongoDBName = process.env.MONGO_DB || 'snmpdb';

let mysqlPool;
let mongoDb;

(async function initializeMySQL() {
  try {
    mysqlPool = await mysql.createPool(mysqlConfig);
    console.log("Connected to MySQL");
  } catch (err) {
    console.error("Error connecting to MySQL:", err);
  }
})();

(async function initializeMongo() {
  try {
    const mongoClient = new MongoClient(mongoUri, { useUnifiedTopology: true });
    await mongoClient.connect();
    mongoDb = mongoClient.db(mongoDBName);
    console.log("Connected to MongoDB");
  } catch (err) {
    console.error("Error connecting to MongoDB:", err);
  }
})();

app.get('/mongoPicture', async (req, res) => {
  try {
    const snmpCollection = mongoDb.collection('snmp_data');
    const snmpData = await snmpCollection.find({}).toArray();
    res.json(snmpData);
  } catch (err) {
    console.error("Error retrieving SNMP data:", err);
    res.status(500).json({ error: "Error retrieving SNMP data" });
  }
});

app.get('/picture', async (req, res) => {
  try {
    const [rows] = await mysqlPool.query('SELECT image_data FROM pictures ORDER BY id DESC LIMIT 1');
    if (rows.length === 0) {
      return res.status(404).send("No picture found");
    }
    const imageBuffer = rows[0].image_data;
    res.writeHead(200, {
      'Content-Type': 'image/bmp',
      'Content-Length': imageBuffer.length
    });
    res.end(imageBuffer);
  } catch (err) {
    console.error("Error retrieving picture from MySQL:", err);
    res.status(500).json({ error: "Error retrieving picture from MySQL" });
  }
});

app.listen(port, () => {
  console.log(`Container C06 REST API server listening on port ${port}`);
});