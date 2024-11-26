package com.example.chambua.presentation.task

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chambua.presentation.components.DeleteDialog
import com.example.chambua.presentation.components.TaskCheckBox
import com.example.chambua.presentation.components.TaskDatePicker
import com.example.chambua.presentation.components.subjectsList
import com.example.chambua.presentation.theme.Red
import com.example.chambua.subjects
import com.example.chambua.util.Priority
import com.example.chambua.util.changeMillisToDateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.time.Instant


data class TaskScreenNav(
    val taskId: Int?,
    val subjectId: Int?
)
@Destination(navArgsDelegate =  TaskScreenNav::class )
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
)
{
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackButton = {navigator.navigateUp()}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    onBackButton: () -> Unit
){
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false)}
    var titleError by rememberSaveable { mutableStateOf<String?>(null)  }
    var isDatePickerOpen by rememberSaveable { mutableStateOf(false)}
    var isDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


    titleError = when   {
        state.title.isBlank()-> "Please enter task title"
        state.title.length < 4 -> "Title is too short"
        state.title.length > 20 -> "Title is too big"
        else -> null
    }
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task",
        bodyText = "Are you sure you want to delete this task?",
        onDismissRequest ={isDeleteDialogOpen = false},
        onDeleteButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteDialogOpen = true}
    )
    TaskDatePicker(
        state = isDatePickerState,
        isOpen = isDatePickerOpen,
        onDismissRequest = {isDatePickerOpen = false},
        onConfirmClick = {
            onEvent(TaskEvent.OnDateChange(millis = isDatePickerState.selectedDateMillis))
            isDatePickerOpen = false}

    )
    subjectsList(
        sheetState = sheetState,
        isOpen = isSheetOpen ,
        subjects = state.subjects,
        onSubjectClicked = {
            subject->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isSheetOpen = false
            }
            onEvent(TaskEvent.OnRelatedSubject(subject))
        },
        onDismissRequest = {isSheetOpen= false}
    )
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color,
                onBackButton = onBackButton,
                onDeleteButton = {isDeleteDialogOpen = true},
                onCheckboxCLick = {onEvent(TaskEvent.OnIsComplete)}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {onEvent(TaskEvent.OnTitleChange(it))},
                label = { Text(text = "Title")},
                isError = titleError != null && state.title.isNotBlank(),
                supportingText = {Text(text = titleError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {onEvent(TaskEvent.OnDescriptionChange(it))},
                label = { Text(text = "Description")}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Due date",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isDatePickerOpen = true}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                                contentDescription = "Selected Due Date"
                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Priority",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach{
                    priority ->
                        PriorityButton(
                            modifier = Modifier.weight(1f),
                            label = priority.title,
                            backgroundColor = priority.color,
                            borderColor = if(priority == state.priority){Color.White}else Color.Transparent,
                            labelColor = if(priority ==state.priority){Color.White}else Color.White.copy(alpha = 0.7f),
                            onClick = {onEvent(TaskEvent.OnPriorityChange(priority))}
                        )
                }
            }
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = "Related to Subject",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isSheetOpen = true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )

                }

            }
            Button(
                enabled = titleError == null,
                onClick = {onEvent(TaskEvent.SaveTask)},
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp)

            ){
                Text(text = "Save")
            }






        }

    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExist: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButton: () ->Unit,
    onDeleteButton: ()->Unit,
    onCheckboxCLick: ()->Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButton) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
            }
        },
        title = {Text(text="Task", style = MaterialTheme.typography.headlineSmall)},
        actions = {
            if(isTaskExist){
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckboxCLick
                )
                IconButton(onClick = onDeleteButton) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task"
                    )
                }

            }
        }
    )
}


@Composable
fun PreviewTaskScreen(){
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                isTaskExist = true,
                isComplete = false,
                checkBoxBorderColor = Red,
                onBackButton = {},
                onDeleteButton = {}
            ) { }
        }
    ){paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {

        }

    }
}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: ()->Unit
){
    Box(
        modifier = modifier.background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(1.dp,borderColor,RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center

    ){
        Text(text = label, color = labelColor)
    }

}