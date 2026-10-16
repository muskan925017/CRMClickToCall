# LeadZam CRM Click-to-Call

A simple CRM click-to-call application that allows users to send a call request from a web dashboard to an Android device using Firebase Cloud Messaging (FCM). When the user taps the **Call** action in the Android notification, the application opens the native phone dialer with the lead's phone number prefilled.

The user must manually place the call. The application does not make phone calls automatically.

## Features

- CRM dashboard displaying sample leads, phone numbers, and lead stages.
- Call request submission from the web dashboard.
- Node.js and Express backend API.
- Firebase Cloud Messaging integration for Android notifications.
- Native Android application developed with Kotlin.
- Notification action that opens the native dialer with the lead's phone number prefilled.
- Request status feedback, loading states, and basic error handling.
- Validation for invalid phone numbers.
- Firebase Admin SDK integration on the backend.
- Firebase service-account credentials excluded from Git.

## Technology Stack

- **Web:** React, Vite, JavaScript, CSS
- **Backend:** Node.js, Express.js
- **Android:** Kotlin, Android SDK
- **Push notifications:** Firebase Cloud Messaging (FCM)
- **Firebase server integration:** Firebase Admin SDK
- **Version control:** Git and GitHub

## Project Structure

```text
CRMClickToCall/
├── android-app/       # Kotlin Android application
├── crm-web/           # React web dashboard
├── server/            # Node.js and Express API
└── README.md
```

## Prerequisites

Install the following tools before running the project:

- Node.js and npm
- Android Studio and Android SDK
- A compatible Java Development Kit (JDK) for the project's Gradle configuration
- Git
- A physical Android device with USB debugging enabled
- A Firebase project with Firebase Cloud Messaging configured

## Setup Instructions

### 1. Configure Firebase

1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Create or select a Firebase project.
3. Register an Android application using the package name configured in the Android project.
4. Download the `google-services.json` configuration file.
5. Place the file in:

   ```text
   android-app/app/google-services.json
   ```

6. Ensure Firebase Cloud Messaging is enabled and configured for the project.
7. Generate a Firebase service-account key for server-side Firebase Admin SDK access.

**Security:** Do not commit the Firebase Admin service-account JSON file, private keys, `.env` files, or device registration tokens to GitHub. Each developer should configure their own Firebase project and use the matching Android configuration file.

### 2. Configure the Backend

Open a terminal in the project root and run:

```powershell
cd server
npm install
```

Create a `.env` file inside the `server/` directory:

```env
PORT=5000
FCM_DEVICE_TOKEN=YOUR_ANDROID_FCM_TOKEN
```

Replace `YOUR_ANDROID_FCM_TOKEN` with the registration token displayed by the Android application.

Place the Firebase service-account JSON file at:

```text
server/firebase-service-account.json
```

The backend uses this file to authenticate with Firebase Admin SDK. Keep it private and excluded from Git.

Start the backend:

```powershell
npm run dev
```

The backend should run on port `5000`.

Check the health endpoint in your browser:

```text
http://localhost:5000/api/health
```

A successful response confirms that the backend is running.

**Important:** Keep the backend terminal open while testing the application.

### 3. Configure and Run the Web Dashboard

Open a second terminal from the project root:

```powershell
cd crm-web
npm install
npm run dev
```

Open the local URL printed by Vite in the terminal, typically:

```text
http://localhost:5173
```

The dashboard sends call requests to:

```text
http://localhost:5000/api/calls
```

Make sure the backend is running before testing the Call button.

The current dashboard uses sample lead data. The API URL is configured for local development.

**Network note:** `localhost` refers to the computer running the browser. If you access the dashboard from a different computer or device, update the API URL and configure the backend's network access and CORS settings appropriately.

### 4. Configure and Run the Android Application

Open the `android-app` directory in Android Studio and allow Gradle synchronization to finish.

Before building, confirm that your Firebase configuration file is present at:

```text
android-app/app/google-services.json
```

Connect your physical Android device with USB debugging enabled and approve the debugging prompt on the device.

Open a terminal in the `android-app/` directory and install the debug build:

```powershell
.\gradlew.bat installDebug
```

Alternatively, build the debug APK without installing it:

```powershell
.\gradlew.bat assembleDebug
```

The generated APK is normally located at:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

Open the installed application and grant notification permission when prompted.

Use the application's FCM token screen to retrieve and copy the Android device registration token.

Add this token to the backend's `server/.env` file:

```env
FCM_DEVICE_TOKEN=YOUR_ANDROID_FCM_TOKEN
```

Restart the backend after changing the environment file so that it loads the updated token.

**Important:** FCM registration tokens are sensitive device identifiers. Do not publish them in your README, source code, screenshots, or public GitHub repository.

## How It Works

1. The user selects a lead and clicks **Call** on the web dashboard.
2. The React application sends the lead ID, contact name, and phone number to the Express API.
3. The backend validates the request and checks the configured FCM device token.
4. The backend sends an FCM data message to the registered Android device.
5. The Android Firebase messaging service receives the message and displays a notification.
6. The user taps the notification's **Call** action.
7. Android opens the native phone dialer with the lead's number prefilled.
8. The user decides whether to place the call.

The application does not automatically place calls. Opening the dialer does not initiate a phone call.

## API Endpoints

### Health Check

**Endpoint:** `GET /api/health`

Checks whether the backend is running.

Example URL:

```text
http://localhost:5000/api/health
```

### Send a Call Request

**Endpoint:** `POST /api/calls`

Sends a call request for processing by the backend.

Example request body:

```json
{
  "leadId": "test-lead-001",
  "name": "Test Lead",
  "phone": "+919876543210"
}
```

The phone number must be valid according to the backend's validation rules.

A successful response indicates that the backend accepted the request and Firebase accepted the message for sending. It does not guarantee that the Android device received or displayed the notification.

Invalid requests should return an appropriate error response. Missing FCM configuration and Firebase send failures should also be handled without reporting a successful send.

## Testing Checklist

Use the following checklist when testing the application:

- [ ] Confirm that `GET /api/health` responds successfully.
- [ ] Start the backend and web dashboard.
- [ ] Open the Android application and confirm that an FCM token is available.
- [ ] Configure the token in `server/.env` and restart the backend.
- [ ] Click **Call** for a sample lead in the browser.
- [ ] Confirm that the Android device receives the notification.
- [ ] Tap the notification's **Call** action.
- [ ] Verify that the native dialer opens with the correct number prefilled.
- [ ] Test notifications while the Android app is in the foreground.
- [ ] Test notifications while the Android app is in the background.
- [ ] Stop the backend and confirm that the dashboard reports a request error.
- [ ] Restart the backend and verify that requests work again.
- [ ] Submit an invalid phone number and confirm that the API rejects it.
- [ ] Test missing or invalid FCM configuration.
- [ ] Test Firebase send failures and repeated call requests.

Some failure scenarios may require direct API requests or temporary test configuration rather than using the sample dashboard.

## Security

The following files and directories must not be committed to Git:

- `.env` files
- `server/firebase-service-account.json`
- `android-app/app/google-services.json`
- `node_modules/`
- Other private keys, credentials, or sensitive device tokens

The repository's `.gitignore` excludes the Firebase configuration and secret files used during local development.

Before publishing, verify that sensitive files are not tracked:

```powershell
git ls-files | Select-String "(\.env|firebase-service-account\.json|google-services\.json)"
```

This command should return no output for those filenames.

**Note:** This check only verifies currently tracked filenames. Review the repository's commit history as well. If a private key or credential was previously committed, deleting it in a later commit does not remove it from Git history. Revoke exposed credentials and replace them if necessary.

## Current Limitations

- The dashboard uses sample lead data rather than a persistent CRM database.
- Call requests are not stored in a persistent database.
- The backend uses a configured FCM device token.
- The FCM token must be updated if the Android device registration token changes.
- The accepted status indicates that Firebase accepted a message for sending; it does not guarantee device delivery.
- Notification delivery depends on Firebase configuration, device connectivity, and Android notification settings.
- The app opens the native dialer but does not place calls automatically.
- The application is intended as a practical assessment project, not a production-ready CRM system.

## AI Usage Notes

### 1. Which AI tools did you use?

I used ChatGPT as an AI assistant during development to understand implementation steps, troubleshoot errors, review code changes, and prepare documentation.

### 2. Where did AI help you the most?

AI helped most with troubleshooting Firebase configuration and Gradle build issues, integrating the Express backend with Firebase Cloud Messaging, and implementing the Android notification action that opens the native dialer.

### 3. Describe one AI suggestion you changed, rejected, or improved. Why?

During testing, I verified that the browser request should report that the backend accepted the request for sending rather than claiming that the Android device definitely received it. This distinction is important because successful FCM message submission does not guarantee device delivery.

### 4. What would you improve with another two hours?

I would improve automated testing for invalid phone numbers, missing or invalid FCM tokens, Firebase send failures, and repeated call requests. I would also improve the setup documentation and test the application on additional Android devices.

*Review these answers and adjust them if needed so they accurately reflect your own development experience.*

## License

This project was created as an Android Developer Intern practical assessment.
