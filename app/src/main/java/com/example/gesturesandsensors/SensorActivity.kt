package com.example.gesturesandsensors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startActivity
import com.example.gesturesandsensors.ui.theme.GesturesAndSensorsTheme
import java.util.Locale

/**
 * Tag for logging purposes.
 */
val TAG = "Sensor Activity"

/**
 * Permission request code for location access.
 */
val PERMISSION_REQUEST_CODE = 123

/**
 * The main activity for handling sensors and gestures in the application.
 */
class SensorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GesturesAndSensorsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SensorContent()
                }
            }
        }
    }
}

/**
 * Composable function representing the content of the [SensorActivity].
 */
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorContent() {
    /** State variables for sensor readings and location information */
    var temperature by remember { mutableStateOf(0.0) }
    var airPressure by remember { mutableStateOf(0.0) }
    var state_output by remember { mutableStateOf("") }
    var city_output by remember { mutableStateOf("") }

    /** Accessing the sensor manager from the current context */
    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /** Retrieving pressure and temperature sensors */
    val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    /** Accessing the current context */
    val location_current = LocalContext.current

    /** State variables for location manager and location permission status */
    var locationManager: LocationManager? by remember { mutableStateOf(null) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    /** Requesting location permission */
    (location_current as? Activity)?.requestPermissions(
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_REQUEST_CODE
    )

    /** Disposable effect for registering and unregistering sensor and location listeners */
    DisposableEffect(sensorManager, pressureSensor, temperatureSensor) {
        /** Sensor event listener for handling changes in sensor readings */
        val sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> {
                        airPressure = event.values[0].toDouble()
                    }
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        temperature = event.values[0].toDouble()
                        Log.d("Sensor Activity", "temperature: $temperature")
                    }
                }
            }
        }

        /** Registering listeners for pressure and temperature sensors if available */
        if (pressureSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                pressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        if (temperatureSensor != null) {
            Log.d("Sensor Activity", "temperature available")
            sensorManager.registerListener(
                sensorEventListener,
                temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        /** Initializing location manager if location permission is granted */
        locationManager = location_current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkSelfPermission(location_current, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true

            /** Location listener for handling changes in location */
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    /** Using Geocoder to obtain city and state information based on latitude and longitude */
                    val geocoder = Geocoder(location_current, Locale.getDefault())
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    Log.d("Sensor Activity", "$addresses")
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val city = addresses[0].locality
                            val state = addresses[0].adminArea

                            state_output = "$state"
                            city_output = "$city"
                            Log.d("Sensor Activity", "$state_output")
                            Log.d("Sensor Activity", "$city_output")
                        }
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            /** Requesting location updates */
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )

            /** Disposing location updates when the composable is no longer active */
            onDispose {
                locationManager?.removeUpdates(locationListener)
            }
        } else {
            Log.d("Sensor Activity", "checkSelfPermission Failed")
        }

        /** Disposing sensor listeners when the composable is no longer active */
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    /** UI layout for displaying sensor readings and buttons */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        /** TopAppBar for displaying the app title */
        TopAppBar(
            title = { Text("Sensor Playground") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        /** Displaying location information */
        Text(
            text = "Location:",
            color = Color(0xFF6F5A86),
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 2.dp)
        )

        Text(
            text = "City: $city_output",
            color = Color(0xFF6F5A86),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 2.dp)
        )

        Text(
            text = "State: $state_output",
            color = Color(0xFF6F5A86),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 32.dp)
        )

        /** Displaying sensor readings */
        Text(
            text = "Temperature: $temperature",
            color = Color(0xFF6F5A86),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 32.dp)
        )

        Text(
            text = "Air Pressure: $airPressure",
            color = Color(0xFF6F5A86),
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 50.dp)
        )

        /** Button for navigating to the GestureActivity */
        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, gestureRotation ->
                        /** Check for a fling operation */
                        if (pan != Offset(0f, 0f)) {
                            /** Fling operation detected, perform your action here */
                            navigateToGestureActivity(location_current)
                            Log.d(TAG, "Fling detected")
                        }
                    }
                }
        ) {
            Text("GESTURE PLAYGROUND")
        }

        /** Button for navigating to the SensorGestureActivity */
        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, gestureRotation ->
                        if (pan != Offset(0f, 0f)) {
                            navigateToSensorGestureActivity(location_current)
                            Log.d(TAG, "Fling detected")
                        }
                    }
                }
        ) {
            Text("GESTURE USING SENSOR PLAYGROUND")
        }
    }
}

/**
 * Navigates to the [GestureActivity].
 *
 * @param context The context used for starting the activity.
 */
fun navigateToGestureActivity(context: Context) {
    val intent = Intent(context, GestureActivity::class.java)
    startActivity(context, intent, null)
}

/**
 * Navigates to the [SensorGestureActivity].
 *
 * @param context The context used for starting the activity.
 */
fun navigateToSensorGestureActivity(context: Context) {
    val intent = Intent(context, SensorGestureActivity::class.java)
    startActivity(context, intent, null)
}

/**
 * Preview function for the [SensorContent]. Displays a sample preview of the sensor activity content.
 */
@Preview(showBackground = true)
@Composable
fun PreviewSensorActivity() {
    GesturesAndSensorsTheme {
        SensorContent()
    }
}
