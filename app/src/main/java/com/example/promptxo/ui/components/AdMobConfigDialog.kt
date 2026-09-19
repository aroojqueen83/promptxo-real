package com.example.promptxo.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.promptxo.ads.AdMobConfig
import com.example.promptxo.ads.AdMobManager
import com.example.ui.theme.PromptXoBorder
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoSearchBg
import com.example.ui.theme.PromptXoSurface
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextSecondary

@Composable
fun AdMobConfigDialog(
    adMobManager: AdMobManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var interstitialId by remember { mutableStateOf(AdMobConfig.INTERSTITIAL_AD_UNIT_ID) }
    var rewardedId by remember { mutableStateOf(AdMobConfig.REWARDED_AD_UNIT_ID) }
    var bannerId by remember { mutableStateOf(AdMobConfig.BANNER_AD_UNIT_ID) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PromptXoSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PromptXoBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AdMob & Integration Setup",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Configure or test your Google AdMob Ad Units. Google official test unit IDs are prefilled for seamless verification.",
                    color = PromptXoTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interstitial Ad Unit
                Text("Interstitial Ad Unit ID", color = PromptXoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = interstitialId,
                    onValueChange = {
                        interstitialId = it
                        AdMobConfig.INTERSTITIAL_AD_UNIT_ID = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PromptXoPrimary,
                        unfocusedBorderColor = PromptXoBorder,
                        focusedContainerColor = PromptXoSearchBg,
                        unfocusedContainerColor = PromptXoSearchBg
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rewarded Ad Unit
                Text("Rewarded Ad Unit ID", color = PromptXoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rewardedId,
                    onValueChange = {
                        rewardedId = it
                        AdMobConfig.REWARDED_AD_UNIT_ID = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PromptXoPrimary,
                        unfocusedBorderColor = PromptXoBorder,
                        focusedContainerColor = PromptXoSearchBg,
                        unfocusedContainerColor = PromptXoSearchBg
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Banner/Native Ad Unit
                Text("Banner / Native Ad Unit ID", color = PromptXoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerId,
                    onValueChange = {
                        bannerId = it
                        AdMobConfig.BANNER_AD_UNIT_ID = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PromptXoPrimary,
                        unfocusedBorderColor = PromptXoBorder,
                        focusedContainerColor = PromptXoSearchBg,
                        unfocusedContainerColor = PromptXoSearchBg
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Live Test Actions:",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            adMobManager.showInterstitialAd(activity) {
                                Toast.makeText(context, "Interstitial ad completed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PromptXoPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Interstitial", fontSize = 11.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            adMobManager.showRewardedAd(activity, "Test Rewarded Ad") {
                                Toast.makeText(context, "Reward granted successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Rewarded", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PromptXoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
