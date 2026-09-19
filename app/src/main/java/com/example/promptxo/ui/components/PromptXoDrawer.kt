package com.example.promptxo.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptxo.data.model.AppPolicies
import com.example.ui.theme.PromptXoBorder
import com.example.ui.theme.PromptXoDarkBg
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoSurface
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextPrimary
import com.example.ui.theme.PromptXoTextSecondary

@Composable
fun PromptXoDrawerContent(
    appPolicies: AppPolicies,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current

    fun openUrl(url: String, fallbackMsg: String) {
        try {
            if (url.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, fallbackMsg, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(),
        drawerContainerColor = PromptXoDarkBg,
        drawerContentColor = PromptXoTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
                .statusBarsPadding()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.promptxo_logo_1789837089456),
                    contentDescription = "PromptXo Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, PromptXoBorder, RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append("Prompt")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = PromptXoPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append("Xo")
                            }
                        }
                    )
                    Text(
                        text = "AI Prompts Hub",
                        color = PromptXoTextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = PromptXoBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // 1. WhatsApp Channel
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = "WhatsApp Channel",
                subtitle = "Join our community for daily prompts",
                iconTint = Color(0xFF25D366),
                onClick = {
                    onCloseDrawer()
                    openUrl(appPolicies.whatsappchannel, "WhatsApp channel link not configured yet")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Rate Us
            DrawerMenuItem(
                icon = Icons.Default.Star,
                title = "Rate Us",
                subtitle = "Support PromptXo on Play Store",
                iconTint = Color(0xFFEAB308),
                onClick = {
                    onCloseDrawer()
                    openUrl(appPolicies.rateus, "Rate us on Google Play")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Privacy Policy
            DrawerMenuItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "Data usage & policy terms",
                iconTint = PromptXoPrimary,
                onClick = {
                    onCloseDrawer()
                    openUrl(appPolicies.privatepolicies, "Opening Privacy Policy")
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PromptXoSurface, CircleShape)
                .border(1.dp, PromptXoBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = PromptXoTextMuted,
                fontSize = 12.sp
            )
        }
    }
}
