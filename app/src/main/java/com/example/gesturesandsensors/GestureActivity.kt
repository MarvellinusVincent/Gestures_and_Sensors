// GestureActivity.kt
package com.example.gesturesandsensors

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gesturesandsensors.ui.theme.GesturesAndSensorsTheme

/**
 * The main activity for handling gestures and sensors in the application.
 */
class GestureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GesturesAndSensorsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GestureActivityContent()
                }
            }
        }
    }
}

/**
 * Composable function representing the content of the [GestureActivity].
 */
@Composable
fun GestureActivityContent() {
    val gestureViewModel = viewModel<GestureViewModel>()
    val topFragment = TopFragment(gestureViewModel)
    val bottomFragment = BottomFragment(gestureViewModel)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    GesturesAndSensorsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isLandscape) {
                Row(Modifier.fillMaxSize()) {
                    topFragment.FirstFragment(Modifier.weight(1f))
                    bottomFragment.SecondFragment(Modifier.weight(1f), gestureViewModel)
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    topFragment.FirstFragment(Modifier.weight(1f))
                    bottomFragment.SecondFragment(Modifier.weight(1f), gestureViewModel)
                }
            }
        }
    }
}

/**
 * Preview function for the [GestureActivityContent]. Displays a sample preview of the main activity content.
 */
@Preview(showBackground = true)
@Composable
fun GestureActivityPreview() {
    GestureActivityContent()
}
