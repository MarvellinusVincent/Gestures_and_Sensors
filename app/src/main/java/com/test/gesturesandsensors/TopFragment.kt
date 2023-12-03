// TopFragment.kt
package com.test.gesturesandsensors

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * The [TopFragment] class represents the top fragment of the application,
 * responsible for handling user gestures and displaying relevant information.
 *
 * @param viewModel The [GestureViewModel] used to communicate with the view model.
 */
@OptIn(ExperimentalComposeUiApi::class)
class TopFragment(private val viewModel: GestureViewModel) {

    /**
     * Composable function representing the first fragment of the top section.
     *
     * @param modifier The [Modifier] for styling and layout customization.
     */
    @Composable
    fun FirstFragment(modifier: Modifier) {
        var ballPosition by remember { mutableStateOf(Offset.Zero) }
        var containerSize by remember { mutableStateOf(Size.Zero) }

        var initialTouchPoint by remember { mutableStateOf(Offset.Zero) }
        var initialTouchPointRef by remember { mutableStateOf<Offset?>(null) }
        val location_current = LocalContext.current

        val doubleTapDetector = remember {
            GestureDetector(
                location_current,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        viewModel.addLogEntry("Double Tap")
                        return super.onDoubleTap(e)
                    }
                }
            )
        }

        val centerOfScreen = with(LocalDensity.current) {
            Offset(containerSize.width / 2f, containerSize.height / 2f)
        }

        DisposableEffect(Unit) {
            ballPosition = centerOfScreen
            onDispose { }
        }

        /** Box composable for handling gestures */
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color(0xFFA8D28E))
                .padding(16.dp)
                .pointerInteropFilter { event ->
                    doubleTapDetector.onTouchEvent(event)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialTouchPoint = Offset(event.x, event.y)
                            initialTouchPointRef = initialTouchPoint
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            /** Calculate final position */
                            val finalTouchPoint = Offset(event.x, event.y)
                            val sensitivityFactor = 0.35f
                            val dx = finalTouchPoint.x - (initialTouchPointRef?.x ?: 0f)
                            val dy = finalTouchPoint.y - (initialTouchPointRef?.y ?: 0f)
                            ballPosition = ballPosition.copy(
                                x = ballPosition.x + dx * sensitivityFactor,
                                y = ballPosition.y + dy * sensitivityFactor
                            )
                            /** Call getDirection to get the direction */
                            val direction = getDirection(dx, dy)
                            if (direction != "No movement") {
                                viewModel.addLogEntry(direction)
                                Log.d("TopFragment", "${viewModel.logEntries}")
                            }
                        }
                    }
                    true
                }
        ) {
            /** Ball composable representing the draggable ball */
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .offset((centerOfScreen.x + ballPosition.x).dp, (centerOfScreen.y + ballPosition.y).dp)
                    .background(Color.Red, shape = CircleShape)
            )
            /** Text composable displaying a message */
            Text(
                text = "Gestures Playground",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 30.sp)
            )
        }
    }

    /**
     * Calculates the direction based on the change in X and Y coordinates.
     *
     * @param dx Change in X coordinate.
     * @param dy Change in Y coordinate.
     * @return String representation of the direction.
     */
    private fun getDirection(dx: Float, dy: Float): String {
        val threshold = 10f
        val angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble()))

        return when {
            Math.abs(dx) > threshold || Math.abs(dy) > threshold -> {
                when {
                    angle in -22.5..22.5 -> "Right"
                    angle in 22.5..67.5 -> "Bottom Right"
                    angle in 67.5..112.5 -> "Bottom"
                    angle in 112.5..157.5 -> "Bottom Left"
                    angle in -67.5..-22.5 -> "Top Right"
                    angle in -112.5..-67.5 -> "Top Left"
                    angle in -157.5..-112.5 -> "Top"
                    else -> "Left"
                }
            }
            else -> "No movement"
        }
    }

    /**
     * Composable function representing a preview of the [TopFragment].
     */
    @Composable
    fun TopFragmentPreview() {
        FirstFragment(Modifier.fillMaxSize())
    }
}

/**
 * Composable function representing a preview of the [TopFragment].
 */
@Composable
@Preview
fun TopFragmentPreview() {
    TopFragment(GestureViewModel()).TopFragmentPreview()
}
