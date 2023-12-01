package com.example.gesturesandsensors

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.gesturesandsensors.ui.theme.GesturesAndSensorsTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Tag for logging purposes.
 */
val TAG3 = "Sensor Gesture Activity"

/**
 * State variable representing the position of the ball.
 */
var ballPosition by mutableStateOf(Offset(0f, 0f))

/**
 * The main activity for handling sensor gestures in the application.
 */
class SensorGestureActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var accelerometerListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Setting the screen orientation to portrait */
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /** Setting the content of the activity to the ThirdFragment */
        setContent {
            ThirdFragment()
        }

        /** Initializing sensors */
        initializeSensors()
    }

    override fun onDestroy() {
        super.onDestroy()
        /** Stopping sensor when the activity is destroyed */
        stopSensor()
    }

    /**
     * Initializes the accelerometer sensor and its listener.
     */
    private fun initializeSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometerListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used in this example
            }

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // Handling accelerometer data
                    handleAccelerometerData(it.values[0], it.values[1])
                }
            }
        }

        /** Starting the accelerometer sensor */
        startSensor()
    }

    /**
     * Registers the accelerometer sensor listener.
     */
    private fun startSensor() {
        accelerometer?.let {
            sensorManager.registerListener(
                accelerometerListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    /**
     * Unregisters the accelerometer sensor listener.
     */
    private fun stopSensor() {
        accelerometerListener?.let {
            sensorManager.unregisterListener(it)
        }
    }

    /**
     * Handles the accelerometer data to update the ball's position.
     *
     * @param x Accelerometer value along the X-axis.
     * @param y Accelerometer value along the Y-axis.
     */
    private fun handleAccelerometerData(x: Float, y: Float) {
        /** Adjusting sensitivity for smoother ball movement */
        val sensitivity = 2.0f
        ballPosition = ballPosition.copy(
            x = ballPosition.x + x * sensitivity,
            y = ballPosition.y + y * sensitivity
        )
    }
}

/**
 * Composable function representing the content of the [SensorGestureActivity].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ThirdFragment() {
    /** Box composable containing the ball and text */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFA8D28E))
            .padding(16.dp)
            .pointerInteropFilter { _ -> true }
    ) {
        /** Ball composable with a specified modifier for position */
        Ball(
            modifier = Modifier
                .size(50.dp)
                .offset { IntOffset(ballPosition.x.roundToInt(), ballPosition.y.roundToInt()) }
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
 * Composable function representing a ball drawn on a canvas.
 *
 * @param modifier Modifier for specifying the size and position of the ball.
 */
@Composable
fun Ball(modifier: Modifier = Modifier) {
    /** Canvas composable for drawing the ball */
    Canvas(modifier = modifier) {
        drawCircle(
            color = Color.Red,
            radius = 50f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

/**
 * Preview function for the [ThirdFragment]. Displays a sample preview of the sensor gesture activity content.
 */
@Composable
@Preview
fun ThirdFragmentPreview() {
    ThirdFragment()
}
