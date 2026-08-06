package com.t8rin.imagetoolboxlite.feature.main.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.ContainerShapeDefaults.centerShape
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.ContainerShapeDefaults.topShape
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TitleItem

private const val WEB_LINK = "https://mi-link-web.netlify.app/"

@Composable
fun AuthorLinksSheet(
    visible: MutableState<Boolean>
) {
    val context = LocalContext.current

    SimpleSheet(
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.app_developer_nick),
                icon = Icons.Rounded.Person
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
                    Spacer(Modifier.height(16.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(WEB_LINK)
                                )
                            )
                        },
                        endIcon = Icons.Rounded.Language,
                        shape = topShape,
                        title = "Mi Web",
                        startIcon = Icons.Rounded.Language,
                        subtitle = "mi-link-web.netlify.app"
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            Intent(Intent.ACTION_SENDTO).apply {
                                data =
                                    Uri.parse("mailto:${context.getString(R.string.developer_email)}")
                                context.startActivity(this)
                            }
                        },
                        shape = centerShape,
                        endIcon = Icons.Rounded.Language,
                        title = stringResource(R.string.email),
                        startIcon = Icons.Rounded.AlternateEmail,
                        subtitle = stringResource(R.string.developer_email)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    )
}
