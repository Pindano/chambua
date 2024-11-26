package com.example.chambua.presentation.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chambua.presentation.components.DeleteDialog
import com.example.chambua.presentation.components.studySessionsList
import com.example.chambua.presentation.components.subjectsList
import com.example.chambua.presentation.theme.Red
import com.example.chambua.util.Constants.SERVICE_CANCEL
import com.example.chambua.util.Constants.SERVICE_START
import com.example.chambua.util.Constants.SERVICE_STOP
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination()
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timer: SessionTimer
)
{
    val viewModel: SessionScreenViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionScreen(
        state = state ,
        onEvent = viewModel::onEvent,
        onBackButton ={ navigator.navigateUp()},
        timerService = timer
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    state: SessionState,
    onEvent: (SessionEvent)->Unit,
    onBackButton: () -> Unit,
    timerService: SessionTimer
){
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimeState by timerService.currentTimerState
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false)}

    subjectsList(
        sheetState = sheetState,
        isOpen = isSheetOpen ,
        subjects = state.subjects,
        onSubjectClicked = {subject->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isSheetOpen = false
            }
            onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        },
        onDismissRequest = {isSheetOpen= false}
    )
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session",
        bodyText = "Are you sure you want to delete this session?",
        onDismissRequest ={isDeleteDialogOpen = false},
        onDeleteButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteDialogOpen = true}
    )
    Scaffold(
        topBar = { SessionScreenTopBar(onBackButton = onBackButton)}
    ){paddingValues ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            item {
                sessionTimer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds

                )
            }
            item {
                RelatedToSubject(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = state.relatedToSubject ?: "",
                    selectSubjectClick = {isSheetOpen = true}

                )
            }
            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButton = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = if (currentTimeState == TimerState.STARTED){
                                SERVICE_STOP
                            }  else SERVICE_START
                        )
                    },
                    finishButton = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        onEvent(SessionEvent.SaveSession(duration))
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = SERVICE_CANCEL
                        )
                    },
                    cancelButton = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = SERVICE_CANCEL
                        )
                    },
                    timerState = currentTimeState,
                    seconds = seconds

                )
            }
            studySessionsList(
                sectionTitle = "History",
                sessions = state.sessions,
                onDeleteIconClick = {

                    isDeleteDialogOpen = true
                onEvent(SessionEvent.OnDeleteSessionButtonClick(it))}
            )


        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButton: ()->Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButton){
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go back"
            )
        }
        },
        title = {
            Text(text = "Study Sessions",
                style = MaterialTheme.typography.headlineSmall)
        }

    )

}

@Composable
private fun sessionTimer(
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Row {
            AnimatedContent(targetState =hours,
                label = hours,
                ) {hours->
                Text(
                    text = "$hours:" ,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState =minutes,
                label = minutes,
            ) {minutes->
                Text(
                    text = "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState =seconds,
                label = seconds,
            ) {seconds->
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }

    }

}

@Composable
private fun RelatedToSubject(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectClick: () -> Unit
){
    Column(modifier = modifier){
        Text(
            text = "Related to Subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = selectSubjectClick) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )

            }

        }
    }

}

@Composable
private fun ButtonsSection(
    modifier : Modifier,
    startButton: () -> Unit,
    cancelButton: () -> Unit,
    finishButton: () -> Unit,
    timerState: TimerState,
    seconds: String

){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
    Button(onClick = cancelButton,
        enabled = seconds!="00" && timerState != TimerState.STARTED) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = "Cancel"
        )
    }
        Button(onClick = startButton,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = androidx.compose.ui.graphics.Color.White

                           )) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = when(timerState){
                    TimerState.IDLE -> "Start"
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                }
            )
        }
        Button(onClick = finishButton,
                enabled = seconds!="00" && timerState != TimerState.STARTED
            ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = "End"
            )
        }
    }

}

