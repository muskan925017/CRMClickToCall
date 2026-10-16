
package com.leadzam.crmclicktocall

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

private val PrimaryPurple = Color(0xFF6C4CE8)
private val LightPurple = Color(0xFFF0ECFF)
private val PageBackground = Color(0xFFF7F8FC)
private val MainText = Color(0xFF20233A)
private val SecondaryText = Color(0xFF74788D)
private val SuccessGreen = Color(0xFF168653)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                FcmSetupScreen()
            }
        }
    }
}

@Composable
fun FcmSetupScreen() {
    val context = LocalContext.current

    var token by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                Toast.makeText(
                    context,
                    "Notifications are disabled. Enable them in Settings to receive call alerts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    token = task.result ?: ""
                    hasError = token.isBlank()
                } else {
                    hasError = true
                    Log.e(
                        "FCM",
                        "Unable to retrieve FCM token",
                        task.exception
                    )
                }
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PageBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { safeAreaPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBackground)
                .padding(safeAreaPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(PrimaryPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LZ",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "LeadZam",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                    Text(
                        text = "CLICK-TO-CALL",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Introduction
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 43.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Connect your device",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MainText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Link this phone to your CRM dashboard to receive call requests from your team.",
                fontSize = 14.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            // FCM token card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DEVICE REGISTRATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = SecondaryText
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = when {
                                    isLoading -> "Connecting..."
                                    hasError -> "Setup needs attention"
                                    else -> "Token ready"
                                },
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(
                                    when {
                                        isLoading -> Color(0xFFFFF3D6)
                                        hasError -> Color(0xFFFFE8E8)
                                        else -> Color(0xFFE4F7ED)
                                    }
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 7.dp
                                )
                        ) {
                            Text(
                                text = when {
                                    isLoading -> "LOADING"
                                    hasError -> "ERROR"
                                    else -> "READY"
                                },
                                color = when {
                                    isLoading -> Color(0xFF946200)
                                    hasError -> Color(0xFFB42318)
                                    else -> SuccessGreen
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Firebase Cloud Messaging token",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF4F5FA)
                    ) {
                        Box(modifier = Modifier.padding(14.dp)) {
                            SelectionContainer {
                                Text(
                                    text = when {
                                        isLoading ->
                                            "Retrieving your device token..."
                                        hasError ->
                                            "Token unavailable. Check your Firebase configuration and try again."
                                        else -> token
                                    },
                                    fontSize = 12.sp,
                                    color = if (hasError) {
                                        SecondaryText
                                    } else {
                                        MainText
                                    },
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Copy token button
                    Button(
                        onClick = {
                            if (token.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "FCM token is empty. Please wait and try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                try {
                                    val clipboard =
                                        context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager

                                    val clip = ClipData.newPlainText(
                                        "FCM Token",
                                        token
                                    )

                                    clipboard.setPrimaryClip(clip)

                                    // Verify the clipboard content.
                                    val copiedText =
                                        clipboard.primaryClip
                                            ?.getItemAt(0)
                                            ?.coerceToText(context)
                                            ?.toString()

                                    if (copiedText == token) {
                                        Toast.makeText(
                                            context,
                                            "Token copied. Paste it into your phone's Notes app.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Copy could not be verified. Please select and copy the token manually.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "FCM",
                                        "Clipboard copy failed",
                                        e
                                    )
                                    Toast.makeText(
                                        context,
                                        "Unable to copy token. Please select and copy it manually.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        enabled = token.isNotBlank() &&
                                !isLoading &&
                                !hasError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = Color(0xFFD8D3E9)
                        )
                    ) {
                        Text(
                            text = "▣   Copy FCM Token",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Copy this token into your backend .env file as FCM_DEVICE_TOKEN.",
                        fontSize = 12.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPurple
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "HOW IT WORKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = PrimaryPurple
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    InstructionRow(
                        number = "1",
                        title = "Copy your device token",
                        description = "Register this phone with your backend."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InstructionRow(
                        number = "2",
                        title = "Select a lead on the web",
                        description = "Press Call in the CRM dashboard."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InstructionRow(
                        number = "3",
                        title = "Tap the phone notification",
                        description = "Review the number in the dialer before calling."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Calls are never placed automatically.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InstructionRow(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = PrimaryPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainText
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 18.sp
            )
        }
    }
}
