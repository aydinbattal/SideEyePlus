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

        val reactionTestResults = mutableListOf<ReactionTest>() // Populate this list with your data
        val adapter = ReactionTestAdapter(reactionTestResults)
        recyclerView.adapter = adapter

        // Call your function to fetch reaction test results and update the adapter
        driverService.getReactionTestResults(
            onSuccess = { results ->
                reactionTestResults.clear()
                reactionTestResults.addAll(results)
                adapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                // Handle failure
            }
        )
    }
}
