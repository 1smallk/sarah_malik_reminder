package com.example.sarah_malik_reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sarah_malik_reminder.ui.theme.Sarah_malik_reminderTheme
import java.util.*

data class Reminder(
    val message: String,
    val date: String,
    val time: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sarah_malik_reminderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReminderApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ReminderApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var reminderMessage by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    val reminders = remember { mutableStateListOf<Reminder>() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = reminderMessage,
            onValueChange = { reminderMessage = it },
            label = { Text("Reminder Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text(text = "Select Date")
        }

        if (selectedDate.isNotEmpty()) {
            Text(text = "Selected Date: $selectedDate", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }) {
            Text(text = "Select Time")
        }

        if (selectedTime.isNotEmpty()) {
            Text(text = "Selected Time: $selectedTime", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (reminderMessage.text.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                reminders.add(Reminder(reminderMessage.text, selectedDate, selectedTime))
                snackbarMessage = "Reminder set for $selectedDate at $selectedTime"
                showSnackbar = true
                reminderMessage = TextFieldValue("")
                selectedDate = ""
                selectedTime = ""
            } else {
                snackbarMessage = "Please enter a message, date, and time!"
                showSnackbar = true
            }
        }) {
            Text("Set Reminder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reminders.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                items(reminders) { reminder ->
                    ReminderItem(reminder = reminder, onClear = {
                        reminders.remove(reminder)
                        snackbarMessage = "Reminder cleared"
                        showSnackbar = true
                    })
                }
            }
        } else {
            Text(text = "No reminders set")
        }

        Spacer(modifier = Modifier.height(16.dp))

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun ReminderItem(reminder: Reminder, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Message: ${reminder.message}")
            Text(text = "Date: ${reminder.date}")
            Text(text = "Time: ${reminder.time}")
        }
        Button(onClick = onClear) {
            Text("Clear")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderAppPreview() {
    Sarah_malik_reminderTheme {
        ReminderApp()
    }
}