package com.routeplanner.app.features.home.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
class LeftPanelState {
    val offsetX = Animatable(0f)
    var panelWidthPx by mutableStateOf(0f)
    private var initialized = false

    /** 0f = totalmente cerrado, 1f = totalmente abierto */
    val progress: Float
        get() = if (panelWidthPx == 0f) 0f else (1f + offsetX.value / panelWidthPx).coerceIn(0f, 1f)

    val isOpen: Boolean
        get() = progress > 0.5f

    suspend fun onWidthMeasured(widthPx: Float) {
        panelWidthPx = widthPx
        if (!initialized) {
            offsetX.snapTo(-widthPx) // arranca oculto fuera de pantalla
            initialized = true
        }
    }

    suspend fun open() {
        offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
    }

    suspend fun close() {
        offsetX.animateTo(-panelWidthPx, spring(stiffness = Spring.StiffnessMediumLow))
    }

    suspend fun toggle() {
        if (isOpen) close() else open()
    }

    suspend fun dragBy(deltaPx: Float) {
        val newValue = (offsetX.value + deltaPx).coerceIn(-panelWidthPx, 0f)
        offsetX.snapTo(newValue)
    }

    /** Decide si completar la apertura o el cierre según dónde quedó al soltar */
    suspend fun settle() {
        if (progress > 0.5f) open() else close()
    }
}

@Composable
fun rememberLeftPanelState(): LeftPanelState = remember { LeftPanelState() }

@Composable
fun LeftPanelOverlay(
    state: LeftPanelState,
    modifier: Modifier = Modifier,
    panelWidth: Dp = 280.dp,
    scrimColor: Color = Color.Black.copy(alpha = 0.4f),
    panelContent: @Composable () -> Unit,
    //content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val panelWidthPx = with(density) { panelWidth.toPx() }

    LaunchedEffect(panelWidthPx) {
        state.onWidthMeasured(panelWidthPx)
    }
    // Panel deslizable
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(panelWidth)
            //.background(RoutePlannerTheme.colors.background)
            .zIndex(2f)
            .offset { IntOffset(x = state.offsetX.value.roundToInt(), y = 0) }
            .pointerInput(state) {
                detectHorizontalDragGestures(
                    onDragEnd = { scope.launch { state.settle() } },
                    onDragCancel = { scope.launch { state.settle() } },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { state.dragBy(dragAmount) }
                    }
                )
            }
    ) {
        panelContent()
    }
}