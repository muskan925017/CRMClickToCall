
const {
    getApps,
    initializeApp,
    cert,
} = require("firebase-admin/app");

const { getMessaging } = require("firebase-admin/messaging");
const serviceAccount = require("../firebase-service-account.json");

const firebaseApp = getApps().length > 0
    ? getApps()[0]
    : initializeApp({
        credential: cert(serviceAccount),
    });

const callRequests = [];

const createCallRequest = async (req, res) => {
    try {
        const { leadId, name, phone } = req.body;

        if (
            !leadId ||
            typeof name !== "string" ||
            !name.trim() ||
            typeof phone !== "string"
        ) {
            return res.status(400).json({
                success: false,
                message: "leadId, name, and phone are required",
            });
        }

        const normalizedPhone = phone.replace(/[\s()-]/g, "");

        if (!/^\+?\d{7,15}$/.test(normalizedPhone)) {
            return res.status(400).json({
                success: false,
                message: "Please provide a valid phone number",
            });
        }

        const deviceToken = process.env.FCM_DEVICE_TOKEN;

        if (!deviceToken) {
            return res.status(500).json({
                success: false,
                message: "Android device FCM token is not configured",
            });
        }

        const request = {
            id: Date.now().toString(),
            leadId,
            name: name.trim(),
            phone: normalizedPhone,
            status: "sending",
            createdAt: new Date().toISOString(),
        };

        const messageId = await getMessaging(firebaseApp).send({
            token: deviceToken,
            data: {
                type: "dial_request",
                leadId: String(leadId),
                contactName: name.trim(),
                phoneNumber: normalizedPhone,
            },
            android: {
                priority: "high",
            },
        });

        request.status = "accepted";
        request.fcmMessageId = messageId;
        callRequests.push(request);

        return res.status(201).json({
            success: true,
            message: "Call request sent to Android device",
            request,
        });
    } catch (error) {
        console.error("FCM call request failed:", error.message);

        return res.status(500).json({
            success: false,
            message: "Failed to send call request to Android device",
        });
    }
};

module.exports = { createCallRequest };
