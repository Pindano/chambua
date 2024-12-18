package com.example.chambua.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.chambua.domain.model.Subject


@Composable

fun AddSubjectDialog(
    isOpen: Boolean,
    onDismissRequest:()->Unit,
    selectedColors: List<Color>,
    onColorChange: (List<Color>)-> Unit,
    onSubjectNameChange: (String) -> Unit,
    onGoalHourChange: (String) -> Unit,
    subjectName: String,
    goalHours: String,


    onConfirmButtonClick: ()->Unit
) {
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var goalHoursError by rememberSaveable { mutableStateOf<String?>(null) }

    subjectNameError = when {
        subjectName.isBlank() -> "Please enter a subject name"
        subjectName.length < 2 -> "Subject name is too short"
        subjectName.length > 20 -> "Subject name is too long"
        else -> null
    }
    goalHoursError = when {
        goalHours.isBlank() -> "Please enter a time duration"
        goalHours.toFloatOrNull() == null -> "Invalid number"
        goalHours.toFloat() < 1f -> "Please set at least 1 hour"
        goalHours.toFloat() > 1000f -> "Set a maximum of 1000 hours"
        else -> null
    }
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title ={
                Text(text = "Add subject")
            },
            text = {
                Column {
                    Row (
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Subject.subjectCardColors.forEach{
                            color ->
                                Box(
                                    modifier = Modifier.size(24.dp)
                                        .clip(CircleShape)
                                        .border(width = 1.dp,
                                            color = if(color == selectedColors){Color.Black}else Color.Transparent,
                                            shape = CircleShape)
                                        .background(brush = Brush.verticalGradient(color))
                                        .clickable {  onColorChange(color)}
                                )
                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = {
                            Text(text = "Subject Name")
                        },
                        isError = subjectNameError !=null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty())},
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = onGoalHourChange,
                        label = {
                            Text(text = "Goal Study Hours")
                        },
                        isError = goalHoursError !=null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHoursError.orEmpty())},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true

                    )



                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text="Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick,
                    enabled = subjectNameError == null && goalHoursError == null){
                    Text(text="save")
                }
            }
        )
    }

}