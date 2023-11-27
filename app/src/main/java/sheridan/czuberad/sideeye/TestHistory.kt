package sheridan.czuberad.sideeye

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.Services.DriverService

class TestHistory : AppCompatActivity() {
    private val driverService = DriverService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_history)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val combinedResults = mutableListOf<Any>() // List to hold both reaction test and questionnaire results
        val adapter = CombinedResultAdapter(combinedResults)
        recyclerView.adapter = adapter

        // Call your function to fetch reaction test results and update the adapter
        driverService.getReactionTestResults(
            onSuccess = { reactionTestResults ->
                combinedResults.addAll(reactionTestResults)
                adapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                // Handle failure
            }
        )

        // Call your function to fetch questionnaire results and update the adapter
        driverService.getQuestionnaireResults(
            onSuccess = { questionnaireResults ->
                combinedResults.addAll(questionnaireResults)
                adapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                // Handle failure
            }
        )
    }
}

