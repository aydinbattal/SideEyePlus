package sheridan.czuberad.sideeye.UI

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic

@Composable
fun SessionDetail(sessionID: String?) {


    val viewModel: IndependentDriverLogic = viewModel()

    LaunchedEffect(Unit){
        if (sessionID != null) {
            viewModel.getSessionDetail(sessionID)
        }
    }
    val session = viewModel.sessionDetail.value

    Text("Session Detail Page: SESSION UUID: $sessionID")
    if (session != null) {
        Text("Session Start Date ${session.startSession}")
    }
}