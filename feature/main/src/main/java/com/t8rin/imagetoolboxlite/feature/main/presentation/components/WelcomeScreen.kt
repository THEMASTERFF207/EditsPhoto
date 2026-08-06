package com.t8rin.imagetoolboxlite.feature.main.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolboxlite.core.resources.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onFinished: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
        delay(1800)
        showButton = true
    }

    val logoScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoRotation by animateFloatAsState(
        targetValue = if (animationStarted) 0f else -15f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logoRotation"
    )

    var titleAlpha by remember { mutableFloatStateOf(0f) }
    var titleTranslationY by remember { mutableFloatStateOf(30f) }
    var subtitleAlpha by remember { mutableFloatStateOf(0f) }
    var subtitleTranslationY by remember { mutableFloatStateOf(30f) }

    LaunchedEffect(animationStarted) {
        if (animationStarted) {
            delay(600)
            titleAlpha = 1f
            titleTranslationY = 0f
            delay(400)
            subtitleAlpha = 1f
            subtitleTranslationY = 0f
        }
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (showButton) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_welcome_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .graphicsLayer {
                        rotationZ = logoRotation
                    }
                    .clip(RoundedCornerShape(28.dp))
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Edits Photo",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .graphicsLayer {
                        translationY = titleTranslationY
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Edita, transforma y\npersonaliza tus fotos\ncon facilidad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .alpha(subtitleAlpha)
                    .graphicsLayer {
                        translationY = subtitleTranslationY
                    }
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onFinished,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(buttonAlpha)
                    .scale(buttonScale),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF7B2FF7),
                                    Color(0xFF2196F3)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Empezar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
