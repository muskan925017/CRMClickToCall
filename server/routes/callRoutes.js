
const express = require("express");
const {
    createCallRequest,
} = require("../controllers/callController");

const router = express.Router();

router.post("/", createCallRequest);

module.exports = router;
