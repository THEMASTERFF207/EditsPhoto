package com.t8rin.imagetoolboxlite.presentation.crash_screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.t8rin.imagetoolboxlite.core.resources.BuildConfig
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.settings.presentation.toUiState
import com.t8rin.imagetoolboxlite.core.ui.icons.emoji.Emoji
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Robot
import com.t8rin.imagetoolboxlite.core.ui.shapes.IconShapesList
import com.t8rin.imagetoolboxlite.core.ui.theme.ImageToolboxTheme
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ContextUtils.copyToClipboard
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedFloatingActionButton
import com.t8rin.imagetoolboxlite.core.ui.widget.haptics.customHapticFeedback
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ToastHost
import com.t8rin.imagetoolboxlite.core.ui.widget.other.rememberToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.presentation.AppActivity
import com.t8rin.imagetoolboxlite.presentation.CrashHandler
import java.net.URLEncoder

private const val WHATSAPP_PHONE = "18093419870"

@AndroidEntryPoint
class CrashActivity : CrashHandler() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashReason = getCrashReason()
        val exName = crashReason.split("\n\n")[0].trim()
        val ex = crashReason.split("\n\n").drop(1).joinToString("\n\n")

        val title = "[Bug] App Crash: $exName"
        val deviceInfo =
            "Device: ${Build.MODEL} (${Build.BRAND} - ${Build.DEVICE}), SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}), App: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n\n"
        val body = "$deviceInfo$ex"

        setContent {
            val toastHostState = rememberToastHostState()
            val scope = rememberCoroutineScope()

            val newClip: (String) -> Unit = {
                copyToClipboard(
                    label = getString(R.string.exception),
                    value = it
                )
                scope.launch {
                    toastHostState.showToast(
                        icon = Icons.Rounded.ContentCopy,
                        message = getString(R.string.copied),
                    )
                }
            }

            val settingsState = getSettingsState()

            val isSecureMode = settingsState.isSecureMode
            LaunchedEffect(isSecureMode) {
                if (isSecureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                }
            }

            CompositionLocalProvider(
                LocalSettingsState provides settingsState.toUiState(
                    allEmojis = Emoji.allIcons(),
                    allIconShapes = IconShapesList
                ),
                LocalHapticFeedback provides customHapticFeedback(settingsState.hapticsStrength)
            ) {
                ImageToolboxTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .verticalScroll(rememberScrollState())
                                .displayCutoutPadding(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Icon(
                                imageVector = Icons.Rounded.Robot,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(72.dp)
                                    .statusBarsPadding(),
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Algo salio mal",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Escríbeme por WhatsApp con el error\ny te ayudaré a solucionarlo",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            EnhancedButton(
                                onClick = {
                                    val message = "$title\n\n$body"
                                    val encoded = URLEncoder.encode(message, "UTF-8")
                                    val url = "https://wa.me/$WHATSAPP_PHONE?text=$encoded"
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                        )
                                    )
                                    newClip(title + "\n\n" + body)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .fillMaxWidth()
                                    .height(52.dp),
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White,
                                borderColor = Color(0xFF25D366)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "\uD83D\uDCAC",
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AutoSizeText(
                                        text = "Contactar por WhatsApp",
                                        maxLines = 1,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val interactionSource = remember {
                                MutableInteractionSource()
                            }
                            val pressed by interactionSource.collectIsPressedAsState()

                            val cornerSize by animateDpAsState(
                                if (pressed) 8.dp
                                else 24.dp
                            )
                            ExpandableItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .navigationBarsPadding(),
                                shape = RoundedCornerShape(cornerSize),
                                interactionSource = interactionSource,
                                visibleContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.BugReport,
                                        contentDescription = null,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            top = 16.dp,
                                            bottom = 16.dp
                                        ),
                                        tint = Color.White.copy(alpha = 0.6f)
                                    )
                                    AutoSizeText(
                                        text = exName,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .weight(1f)
                                    )
                                },
                                expandableContent = {
                                    AnimatedVisibility(visible = it) {
                                        SelectionContainer {
                                            Text(
                                                text = ex,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(16.dp),
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                        Row(
                            Modifier
                                .padding(8.dp)
                                .navigationBarsPadding()
                                .displayCutoutPadding()
                                .align(Alignment.BottomCenter)
                        ) {
                            EnhancedFloatingActionButton(
                                modifier = Modifier
                                    .weight(1f, false),
                                onClick = {
                                    startActivity(
                                        Intent(
                                            this@CrashActivity,
                                            AppActivity::class.java
                                        )
                                    )
                                },
                                content = {
                                    Spacer(Modifier.width(16.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.RestartAlt,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    AutoSizeText(
                                        text = stringResource(R.string.restart_app),
                                        maxLines = 1
                                    )
                                    Spacer(Modifier.width(16.dp))
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            EnhancedFloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    newClip(title + "\n\n" + body)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    ToastHost(hostState = toastHostState)
                }
            }
        }
    }

}
