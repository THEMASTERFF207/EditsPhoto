package com.t8rin.imagetoolboxlite.feature.main.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ContextUtils.copyToClipboard
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TitleItem

private const val WHATSAPP_PHONE = "18093419870"

private val topShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

private val bottomShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

@Composable
fun DonateSheet(
    visible: MutableState<Boolean>
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    SimpleSheet(
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.donation),
                icon = Icons.Rounded.Payments
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { visible.value = false },
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .container(color = MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Si deseas apoyar el desarrollo de esta app, contactame por WhatsApp y coordinamos la donacion.",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 14.sp,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                    PreferenceItem(
                        color = Color(0xFF25D366),
                        contentColor = Color.White,
                        shape = topShape,
                        onClick = {
                            val message = "Hola! Quiero donar a la app Edits Photo"
                            val encoded = Uri.encode(message)
                            val url = "https://wa.me/$WHATSAPP_PHONE?text=$encoded"
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        },
                        endIcon = Icons.Rounded.Phone,
                        startIcon = Icons.Rounded.Phone,
                        title = "WhatsApp",
                        subtitle = "+1 (809) 341-9870"
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = bottomShape,
                        onClick = {
                            context.copyToClipboard(
                                label = "WhatsApp",
                                value = "+1 (809) 341-9870"
                            )
                            scope.launch {
                                toastHostState.showToast(
                                    icon = Icons.Rounded.ContentCopy,
                                    message = context.getString(R.string.copied),
                                )
                            }
                        },
                        endIcon = Icons.Rounded.ContentCopy,
                        startIcon = Icons.Rounded.ContentCopy,
                        title = "Copiar numero",
                        subtitle = "+1 (809) 341-9870"
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    )
}
