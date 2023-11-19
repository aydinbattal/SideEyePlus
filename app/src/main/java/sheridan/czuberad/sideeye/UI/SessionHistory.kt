package sheridan.czuberad.sideeye.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SessionHistory() {
    





    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Session History", fontSize = 30.sp, modifier = Modifier.padding(10.dp))

            SessionList()




    }
}



@Composable
fun SessionList(){

    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
    
    LazyColumn{
        items(items){item ->
            SessionListItem(item)
        }
    }
    

}

@Composable
fun SessionListItem(item: String) {
    
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
        Text(text = item)
        
    }

}
