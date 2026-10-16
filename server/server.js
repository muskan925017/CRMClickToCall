const express = require("express");
const cors = require("cors");
require("dotenv").config();

const app = express();
const PORT = process.env.PORT || 5000;
const callRoutes = require("./routes/callRoutes");

app.use(cors());
app.use(express.json());
app.use("/api/calls", callRoutes);

// Root endpoint
app.get("/", (req, res) => {
    res.json({
        success: true,
        message: "CRM Click-to-Call API is running!",
    });
});

// Health-check endpoint
app.get("/api/health", (req, res) => {
    res.json({
        success: true,
        message: "Backend is healthy",
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
