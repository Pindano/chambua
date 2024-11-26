package com.example.chambua.presentation.components

import android.widget.DatePicker
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state:DatePickerState,
    isOpen: Boolean,
    confirmButton: String ="Ok",
    dismissButton: String = "Cancel",
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit

){
    if (isOpen){
        DatePickerDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(text = "confirm")
                }
            },

            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            content = {
                DatePicker(state = state,
                    dateValidator = {timestamp->
                        val selectedDate = Instant
                            .ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val currentDate = LocalDate.now(ZoneId.systemDefault())
                        selectedDate >= currentDate
                    }
                )
            }
        )
    }
}