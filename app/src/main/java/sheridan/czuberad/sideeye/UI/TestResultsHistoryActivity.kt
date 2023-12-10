package sheridan.czuberad.sideeye.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import sheridan.czuberad.sideeye.Adapters.CombinedResultAdapter
import sheridan.czuberad.sideeye.Domain.Questionnaire
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.Services.DriverService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TestResultsHistoryActivity : AppCompatActivity() {
    private val driverService = DriverService()
    val combinedResults = mutableListOf<Any>() // List to hold both reaction test and questionnaire results
    var reactionTestResults: List<ReactionTest>? = null
    var questionnaireResults: List<Questionnaire>? = null
    val adapter = CombinedResultAdapter(combinedResults)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_results_history)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        // Launch a coroutine to fetch reaction test results
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Suspend functions to fetch reaction test and questionnaire results concurrently
                val reactionTestDeferred = async(Dispatchers.IO) { driverService.getReactionTestResultsAsync() }
                val questionnaireDeferred = async(Dispatchers.IO) { driverService.getQuestionnaireResultsAsync() }

                // Wait for both results
                reactionTestResults = reactionTestDeferred.await()
                questionnaireResults = questionnaireDeferred.await()

                // Combine and display results
                combineAndDisplayResults()
            } catch (e: Exception) {
                // Handle failure
            }
        }
    }

    private fun combineAndDisplayResults() {
        // Combine the results by session
        val combinedResultsBySession =
            combineResultsBySession(reactionTestResults!!, questionnaireResults!!)

        // Update the combinedResults list
        combinedResults.clear()
        combinedResults.addAll(combinedResultsBySession)

        // Notify the adapter of the data change
        adapter.notifyDataSetChanged()
    }

    private fun combineResultsBySession(
        reactionTestResults: List<ReactionTest>,
        questionnaireResults: List<Questionnaire>
    ): List<Any> {
        val combinedResultsMap = mutableMapOf<String, MutableList<Any>>()

        // Combine reaction test results by session
        reactionTestResults.forEach { reactionTest ->
            val sessionId = reactionTest.sessionId
            if (sessionId != null) {
                combinedResultsMap.computeIfAbsent(sessionId) { mutableListOf() }.add(reactionTest)
            }
        }

        // Combine questionnaire results by session
        questionnaireResults.forEach { questionnaire ->
            val sessionId = questionnaire.sessionId
            if (sessionId != null) {
                combinedResultsMap.computeIfAbsent(sessionId) { mutableListOf() }.add(questionnaire)
            }
        }

        //todo: Sort reaction test results within each session by timestamp in descending order
//        combinedResultsMap.values.forEach { resultList ->
//            resultList.sortByDescending {
//                when (it) {
//                    is ReactionTest -> it.date // Assuming ReactionTest has a 'timestamp' property
//                    else -> error("Unexpected result type: ${it.javaClass.simpleName}")
//                }
//            }
//        }

        // Combine results by session
        val combinedResults = mutableListOf<Any>()
        combinedResultsMap.forEach { (_, resultList) ->
            combinedResults.addAll(resultList)
        }

        return combinedResults
    }
}

// Extension functions for suspending the fetch operations
suspend fun DriverService.getReactionTestResultsAsync(): List<ReactionTest> =
    suspendCoroutine { continuation ->
        getReactionTestResults(
            onSuccess = { reactionTestResults ->
                continuation.resume(reactionTestResults)
            },
            onFailure = { exception ->
                continuation.resumeWithException(exception)
            }
        )
    }

suspend fun DriverService.getQuestionnaireResultsAsync(): List<Questionnaire> =
    suspendCoroutine { continuation ->
        getQuestionnaireResults(
            onSuccess = { questionnaireResults ->
                continuation.resume(questionnaireResults)
            },
            onFailure = { exception ->
                continuation.resumeWithException(exception)
            }
        )
    }