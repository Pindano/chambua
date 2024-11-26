package com.example.chambua.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chambua.R
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task
import com.example.chambua.presentation.Subject.SubjectScreenNav
import com.example.chambua.presentation.components.AddSubjectDialog
import com.example.chambua.presentation.components.CountCard
import com.example.chambua.presentation.components.DeleteDialog
import com.example.chambua.presentation.components.studySessionsList
import com.example.chambua.presentation.components.taskList
import com.example.chambua.presentation.destinations.SessionScreenRouteDestination
import com.example.chambua.presentation.destinations.SubjectScreenRouteDestination
import com.example.chambua.presentation.destinations.TaskScreenRouteDestination
import com.example.chambua.presentation.task.TaskScreenNav
import com.example.chambua.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        tasks = tasks,
        recentSessions = recentSessions,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackbarEventFlow,
        onSubjectClick = { subjectId ->
            subjectId?.let {
                val navArg = SubjectScreenNav(subjectId = subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
            }
        },
        onTaskClick = { taskId ->
            val navArg = TaskScreenNav(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onStartSession = {
            navigator.navigate(SessionScreenRouteDestination())
        }

    )
}

@Composable
private fun DashboardScreen(

    state: DashboardStates,
    tasks: List<Task>,
    recentSessions: List<Session>,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (DashboardEvent) -> Unit,
    onSubjectClick: (Int?) -> Unit,
    onTaskClick: (Int?) -> Unit,
    onStartSession: ()-> Unit
){

    val snackbarHostState = remember {SnackbarHostState() }
    var isAddSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event->
            when(event){
            is SnackBarEvent.ShowSnackBar -> {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = event.duration
                )
            }

        }


        }
    }


    AddSubjectDialog(
        isOpen =isAddSubjectDialog,
        onDismissRequest = {isAddSubjectDialog = false},
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange ={onEvent(DashboardEvent.onSubjectNameChange(it))} ,
        onGoalHourChange = {onEvent(DashboardEvent.onGoalStudyHoursChange(it))} ,
        selectedColors = state.subjectCardColors,
        onColorChange = {onEvent(DashboardEvent.onSubjectColorChange(it))},
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialog =false }
    )
    DeleteDialog(
        title = "Delete Session",
        bodyText = "Are you sure you want to delete a session",
        isOpen = isDeleteDialog,
        onDismissRequest = {isDeleteDialog = false},
        onDeleteButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteDialog = false}
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = {DashboardScreenTopBar()}
    ){paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){ item {
            CountCards(
                modifier = Modifier.fillMaxWidth()
                            .padding(12.dp),
                subjectCount = state.totalSubjectCount,
                studiedHours = state.totalStudiedHours.toString(),
                goalHours = state.totalGoalsStudyHours.toString()
            )
        }
            item{
                Spacer(modifier = Modifier.height(20.dp))
            }
            item{
                SubjectCard(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClick = {
                        isAddSubjectDialog = true
                    },
                    onSubjectClick = onSubjectClick
                )
            }
            item{
                Button(
                    onClick = onStartSession,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)

                ){
                    Text(text = "Start Study")
                }
            }
            taskList(
                sectionTitle = "Upcoming Tasks",
                tasks = tasks,
                onCheckBoxClick = {onEvent(DashboardEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskClick,

            )
            item{
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionsList(
                sectionTitle = "Recent study sessions",
                sessions = recentSessions,
                onDeleteIconClick = {
                    onEvent(DashboardEvent.onDeleteSessionClick(it))
                    isDeleteDialog = true
                }
            )
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(title = {
        Text(
            text = "Chambua",
            style = MaterialTheme.typography.headlineMedium
        )
    })
}
@Composable
private fun CountCards(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
){
    Row{
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count =goalHours
        )
    }
}

@Composable
private fun SubjectCard(
    modifier: Modifier,
    subjectList: List<Subject>,
    onAddIconClick: () -> Unit,
    onSubjectClick: (Int?)-> Unit
){
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Text(
                text = "Subjects",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddIconClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }
            if (subjectList.isEmpty()){
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    painter = painterResource(R.drawable.img_books),
                    contentDescription = "No subjects found. Press + to add new subjects"
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "No subjects found. Press + to add new subjects",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                items(subjectList){
                    subject ->
                    com.example.chambua.presentation.components.SubjectCard(
                        subjectName = subject.name,
                        gradientColors = subject.colors.map { Color(it) },
                        onclick = {onSubjectClick(subject.subjectId)}
                    )
                }
            }

        }
    }
