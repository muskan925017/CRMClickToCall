//package com.leadzam.crmclicktocall
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.google.firebase.messaging.FirebaseMessaging
//import com.leadzam.crmclicktocall.ui.theme.CRMClickToCallTheme
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//
//class MainActivity : ComponentActivity() {
//    private val notificationPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { granted ->
//            Log.d("FCM_PERMISSION", "Notification permission granted: $granted")
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            notificationPermissionLauncher.launch(
//                Manifest.permission.POST_NOTIFICATIONS
//            )
//        }
//
//        setContent {
//            CRMClickToCallTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    FcmSetupScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun FcmSetupScreen(modifier: Modifier = Modifier) {
//    var token by remember { mutableStateOf("Getting FCM token...") }
//    var message by remember { mutableStateOf("Preparing Firebase connection") }
//
//    LaunchedEffect(Unit) {
//        FirebaseMessaging.getInstance().token
//            .addOnSuccessListener { fcmToken ->
//                token = fcmToken
//                message = "Firebase token retrieved successfully"
//                Log.d("FCM_TOKEN", fcmToken)
//            }
//            .addOnFailureListener { exception ->
//                token = "Token unavailable"
//                message = exception.localizedMessage ?: "Could not get FCM token"
//                Log.e("FCM_TOKEN", "Failed to get token", exception)
//            }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "LeadZam Click-to-Call",
//            style = MaterialTheme.typography.headlineSmall
//        )
//
//        Text(
//            text = "Firebase Device Registration",
//            style = MaterialTheme.typography.titleMedium
//        )
//
//        Text(text = message)
//
//        Text(
//            text = token,
//            style = MaterialTheme.typography.bodySmall
//        )
//
//        Button(
//            onClick = {
//                Log.d("FCM_TOKEN", token)
//            }
//        ) {
//            Text("Log FCM Token")
//        }
//    }
//}


package com.leadzam.crmclicktocall

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.leadzam.crmclicktocall.ui.theme.CRMClickToCallTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            android.util.Log.d(
                "FCM_PERMISSION",
                "Notification permission granted: $granted"
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            CRMClickToCallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FcmSetupScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FcmSetupScreen(modifier: Modifier = Modifier) {
    var token by remember { mutableStateOf("Getting FCM token...") }
    var message by remember {
        mutableStateOf("Preparing Firebase connection")
    }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                token = fcmToken
                message = "Firebase token retrieved successfully"
            }
            .addOnFailureListener { exception ->
                token = ""
                message = exception.localizedMessage
                    ?: "Could not get FCM token"
            }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "LeadZam Click-to-Call",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Firebase Device Registration",
            style = MaterialTheme.typography.titleMedium
        )

        Text(text = message)

        SelectionContainer {
            Text(
                text = token.ifBlank { "Token not available" },
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            enabled = token.isNotBlank() &&
                    token != "Getting FCM token...",
            onClick = {
                val clipboard = context.getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

                clipboard.setPrimaryClip(
                    ClipData.newPlainText("FCM Token", token)
                )

                Toast.makeText(
                    context,
                    "FCM token copied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            Text("Copy FCM Token")
        }
    }
}
