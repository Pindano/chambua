package com.example.chambua.presentation.Subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chambua.presentation.components.AddSubjectDialog
import com.example.chambua.presentation.components.CountCard
import com.example.chambua.presentation.components.DeleteDialog
import com.example.chambua.presentation.components.studySessionsList
import com.example.chambua.presentation.components.taskList
import com.example.chambua.presentation.destinations.TaskScreenRouteDestination
import com.example.chambua.presentation.task.TaskScreenNav
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


data class SubjectScreenNav(
    val subjectId: Int
)
@Destination(navArgsDelegate = SubjectScreenNav::class)

@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
)
{
    val viewModel: SubjectScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SubjectScreen(
        state = state,
        onEvent = viewModel::onEvent,

        onBack = {navigator.navigateUp()},
        onAddTask = {
            val navArg = TaskScreenNav(taskId = null, subjectId = state.currentSubjectId)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))},
        onTaskCardClick = {taskId ->
            val navArg = TaskScreenNav(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

private fun SubjectScreen(
    state: SubjectState,
    onEvent: (SubjectEvent)->Unit,
    onBack: () -> Unit,
    onAddTask:()-> Unit,
    onTaskCardClick: (Int?) -> Unit
){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isEditSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var isDeleteSubjectDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect (key1 = state.studiedHours, key2 = state.goalStudyHours){
        onEvent(SubjectEvent.UpdateProgress)
    }
    AddSubjectDialog(
        isOpen =isEditSubjectDialog,
        onDismissRequest = {isEditSubjectDialog = false},
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange ={onEvent(SubjectEvent.OnSubjectNameChange(it))} ,
        onGoalHourChange = {onEvent(SubjectEvent.OnGoalStudyHours(it))} ,
        selectedColors = state.subjectCardColors,
        onColorChange = {onEvent(SubjectEvent.OnSubjectColorChange(it))},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialog =false }
    )
    DeleteDialog(
        title = "Delete Subject",
        bodyText = "Are you sure you want to delete a subject?",
        isOpen = isDeleteSubjectDialog,
        onDismissRequest = {isDeleteSubjectDialog = false},
        onDeleteButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialog = false
            onBack()
        }
    )
    DeleteDialog(
        title = "Delete Session",
        bodyText = "Are you sure you want to delete a session",
        isOpen = isDeleteDialog,
        onDismissRequest = {isDeleteDialog = false},
        onDeleteButtonClick = {
            onEvent(SubjectEvent.DeleteSession)
            isDeleteDialog = false}
    )
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = state.subjectName,
                onBack = onBack,
                onEdit = {isEditSubjectDialog = true},
                onDelete = {isDeleteSubjectDialog = true},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
                icon = {
                    Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )},
                text = {
                        Text(
                            text = "Add Task"
                        )}
            )
        }
    ) { paddingValue ->

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValue),
        ) {
            item {
                SubjectOverview(
                    modifier = Modifier.fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = state.studiedHours.toString(),
                    goalHours = state.goalStudyHours,
                    progress = state.progress
                )
            }
            taskList(
                sectionTitle = "Upcoming Tasks",
                tasks = state.upcomingTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsComplete(it))},
                onTaskCardClick = onTaskCardClick
            )
            item{
                Spacer(modifier = Modifier.height(20.dp))
            }
            taskList(
                sectionTitle = "Completed Tasks",
                tasks = state.completedTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsComplete(it))},
                onTaskCardClick = onTaskCardClick
            )


            studySessionsList(
                sectionTitle = "Recent study sessions",
                sessions = state.recentSessions,
                onDeleteIconClick = {
                    onEvent(SubjectEvent.OnDeleteSession(it))
                    isDeleteDialog = true}
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreenTopBar(
    title:String,
    onBack: ()->Unit,
    onDelete:()->Unit,
    onEdit:()->Unit,
    scrollBehavior: TopAppBarScrollBehavior
){
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = { Text(
            text = "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall
        )},
        actions = {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Subject"
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }
        }
    )
}

@Composable
private fun SubjectOverview(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float
){
    val percentageProgress = remember(key1 = progress){
        (progress * 100).toInt().coerceIn(0,100)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied hours",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = "$percentageProgress")

        }
    }
}
