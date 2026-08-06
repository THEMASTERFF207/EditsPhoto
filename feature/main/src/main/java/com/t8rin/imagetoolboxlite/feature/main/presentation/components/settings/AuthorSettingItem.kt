package com.t8rin.imagetoolboxlite.feature.main.presentation.components.settings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.ContainerShapeDefaults
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.AuthorLinksSheet

@Composable
fun AuthorSettingItem(
    shape: Shape = ContainerShapeDefaults.topShape
) {
    val showAuthorSheet = rememberSaveable { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val avatarScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "avatarScale"
    )

    PreferenceRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        title = stringResource(R.string.app_developer),
        subtitle = stringResource(R.string.app_developer_nick),
        shape = shape,
        startIcon = Icons.AutoMirrored.Outlined.OpenInNew,
        endContent = {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(64.dp)
                    .scale(avatarScale)
                    .clip(CircleShape)
                    .container(
                        shape = CircleShape,
                        resultPadding = 0.dp
                    ),
                contentDescription = null
            )
        },
        onClick = {
            isPressed = true
            showAuthorSheet.value = true
        }
    )
    AuthorLinksSheet(showAuthorSheet)
}
