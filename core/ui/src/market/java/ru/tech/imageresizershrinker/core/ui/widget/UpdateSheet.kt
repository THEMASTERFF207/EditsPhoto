package com.t8rin.imagetoolboxlite.core.ui.widget

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolboxlite.core.domain.APP_RELEASES
import com.t8rin.imagetoolboxlite.core.domain.update.AutoUpdateManager
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleDragHandle
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.HtmlText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TitleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@Composable
fun UpdateSheet(
    changelog: String,
    tag: String,
    visible: MutableState<Boolean>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }

    SimpleSheet(
        endConfirmButtonPadding = 0.dp,
        visible = visible,
        title = {},
        dragHandle = {
            SimpleDragHandle {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(
                        LocalContentColor.provides(MaterialTheme.colorScheme.onSurface),
                        LocalTextStyle.provides(MaterialTheme.typography.bodyLarge)
                    ) {
                        TitleItem(
                            text = stringResource(R.string.new_version, tag),
                            icon = Icons.Rounded.NewReleases
                        )
                    }
                }
            }
        },
        sheetContent = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                Box {
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            HtmlText(
                                html = changelog.trimIndent(),
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)
                            ) { uri ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    if (isDownloading) return@EnhancedButton

                    isDownloading = true
                    scope.launch {
                        try {
                            val apiUrl = "https://api.github.com/repos/THEMASTERFF207/EditsPhoto/releases/latest"
                            val connection = withContext(Dispatchers.IO) {
                                URL(apiUrl).openConnection().apply {
                                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                                }
                            }
                            val response = withContext(Dispatchers.IO) {
                                connection.inputStream.bufferedReader().readText()
                            }
                            val json = JSONObject(response)
                            val assets = json.getJSONArray("assets")

                            var apkUrl: String? = null
                            for (i in 0 until assets.length()) {
                                val asset = assets.getJSONObject(i)
                                if (asset.getString("name").endsWith(".apk")) {
                                    apkUrl = asset.getString("browser_download_url")
                                    break
                                }
                            }

                            if (apkUrl != null) {
                                AutoUpdateManager.downloadApk(context, apkUrl, tag)
                                visible.value = false
                            }
                        } catch (e: Exception) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("$APP_RELEASES/tag/$tag")
                                )
                            )
                        }
                        isDownloading = false
                    }
                }
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    AutoSizeText(stringResource(id = R.string.update))
                }
            }
        }
    )
}
