// BottomFragment.kt
package com.test.gesturesandsensors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * A Composable function representing the bottom fragment of the application, displaying a list of log entries.
 *
 * @param viewModel An instance of [GestureViewModel] to observe log entries.
 */
class BottomFragment(private val viewModel: GestureViewModel) {
    /**
     * Composable function representing the second fragment, displaying a list of log entries using LazyColumn.
     *
     * @param modifier The [Modifier] for styling and layout customization.
     * @param viewModel An instance of [GestureViewModel] to observe log entries.
     */
    @Composable
    fun SecondFragment(modifier: Modifier, viewModel: GestureViewModel) {
        val logEntriesState = viewModel.logEntries

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            items(logEntriesState) { entry ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = entry)
                }
            }
        }
    }

    /**
     * Composable function providing a preview of the [SecondFragment].
     */
    @Composable
    fun BottomFragmentPreview() {
        SecondFragment(Modifier.fillMaxSize(), viewModel)
    }
}

/**
 * Preview function for the [BottomFragment]. Displays a sample preview of the bottom fragment.
 */
@Preview
@Composable
fun BottomFragmentPreview() {
    BottomFragment(viewModel()).BottomFragmentPreview()
}
