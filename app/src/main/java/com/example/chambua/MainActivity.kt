package com.example.chambua

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task
import com.example.chambua.presentation.NavGraphs
import com.example.chambua.presentation.destinations.SessionScreenRouteDestination
import com.example.chambua.presentation.session.SessionTimer
import com.example.chambua.presentation.theme.ChambuaTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.timer


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timerService: SessionTimer
    private val connection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SessionTimer.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = true
        }

    }
    override fun onStart(){
        super.onStart()
        Intent(this,SessionTimer::class.java).also{
            intent -> bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (isBound){
            ChambuaTheme {
                DestinationsNavHost(navGraph = NavGraphs.root,
                    dependenciesContainerBuilder = {
                        dependency(SessionScreenRouteDestination){timerService}
                    })
            }
        }}
        requestPermission()
    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}



val subjects = listOf(
    Subject(name = "English", goalHours = 10f,colors = Subject.subjectCardColors[0].map { it.toArgb() }, subjectId = 0),
    Subject(name = "Physics", goalHours = 10f,colors = Subject.subjectCardColors[1].map { it.toArgb() },subjectId = 0),
    Subject(name = "Kiswahili", goalHours = 10f,colors = Subject.subjectCardColors[2].map { it.toArgb() },subjectId = 0),
    Subject(name = "Biology", goalHours = 10f,colors = Subject.subjectCardColors[3].map { it.toArgb() },subjectId = 0),
    Subject(name = "Maths", goalHours = 10f,colors = Subject.subjectCardColors[4].map { it.toArgb() },subjectId = 0),

    )
val tasks = listOf(
    Task(
        title = "Prepare notes",
        description="",
        dueDate= 0L,
        priority = 1,
        relatedToSubject = "",
        taskId = 0,
        taskSubjectId = 0,
        isComplete = false),


    Task(
        title = "Cook Fish",
        description="",
        dueDate= 0L,
        priority = 2,
        relatedToSubject = "",
        taskId = 0,
        taskSubjectId = 0,
        isComplete = true),
    Task(
        title = "Cat reading",
        description="",
        dueDate= 0L,
        priority = 3,
        relatedToSubject = "",
        taskId = 0,
        taskSubjectId = 0,
        isComplete = true),
    Task(
        title = "Go shopping",
        description="",
        dueDate= 0L,
        priority = 1,
        relatedToSubject = "",
        taskId = 0,
        taskSubjectId = 0,
        isComplete = false),
)
val sessions = listOf(
    Session(
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
        relatedToSubject = "Physics",
    ),
    Session(
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 1,
        relatedToSubject = "Biology",
    )
)


@Preview
@Composable

private fun PreviewDashboard(){
    ChambuaTheme {
        DestinationsNavHost(navGraph = NavGraphs.root)
    }
}

