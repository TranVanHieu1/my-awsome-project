const express = require("express");
const cors = require("cors");
const proxy = require("express-http-proxy");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/notification", proxy("http://localhost:8082"));
app.use("/payment", proxy("http://localhost:8081"));
app.use("/", proxy("http://localhost:8080")); // products

app.listen(8000, () => {
  console.log("Gateway is Listening to Port 8000");
});
