package sheridan.czuberad.sideeye

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import sheridan.czuberad.sideeye.databinding.ActivityQuestionnaireBinding
import sheridan.czuberad.sideeye.databinding.ActivityReactionTestBinding

class QuestionnaireActivity : AppCompatActivity() {
    private lateinit var questionTextView: TextView
    private lateinit var answerOptionsGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button
    private lateinit var errorMessageTextView: TextView

    private lateinit var binding: ActivityQuestionnaireBinding

    private data class Question(
        val text: String,
        val options: List<Pair<String, String>>,
        var selectedOption: Pair<String, String>? = null)

    private val questions = mutableListOf(
        Question(
            "How long have you been driving since your last break?",
            listOf(
                "Less than two hours" to "Low",
                "Between two and four hours" to "Mild",
                "More than four hours" to "High"
            ),

        ),
        Question(
            "Do you think your hydration and blood sugar is OK?",
            listOf(
                "Yes, and I think it is as good as possible" to "Low",
                "Yes, and I could do with a drink or snack" to "Mild",
                "No" to "High"
                ),

        ),
        Question(
            "Do you believe you are fit to continue work?",
            listOf(
                "Yes" to "Low",
                "Yes, with additional risk controls" to "Mild",
                "No, not right now" to "High"
            ),

        ),
        Question(
            "How do you feel right now?",
            listOf(
                "Very alert – wide awake" to "Low",
                "A bit tired, effort required to stay alert" to "Mild",
                "Very fatigued, having difficulty staying alert" to "High"
            ),

        ),
        Question(
            "Did you sleep in the last 24 hours?",
            listOf(
                "Yes, I got my ideal amount of sleep" to "Low",
                "Yes, but I did not get my ideal amount of sleep" to "Mild",
                "No" to "High"
            ),

        ),
        Question(
            "How would you rate the quality of that sleep compared with what you\n" +
                    "usually get on similar shift patterns?",
            listOf(
                "Good" to "Low",
                "Average" to "Mild",
                "Poor" to "High"
            ),

        ),
        Question(
            "Have you experienced any physical\n" +
                    "signs of fatigue immediately prior to or\n" +
                    "during this shift (e.g. microsleeps or difficulty concentrating)",
            listOf(
                "No" to "Low",
                "Yes" to "High"
            ),

        ),
    )

    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        questionTextView = binding.questionTextView
        answerOptionsGroup = binding.answerOptionsGroup
        nextButton = binding.nextButton
        submitButton = binding.submitButton
        errorMessageTextView = binding.errorMessageTextView

        displayQuestion(currentQuestionIndex)

        nextButton.setOnClickListener {
            val selectedOptionId = answerOptionsGroup.checkedRadioButtonId
            if (selectedOptionId != -1) { // User selected an option
                val selectedOptionIndex = answerOptionsGroup.indexOfChild(findViewById(selectedOptionId))
                questions[currentQuestionIndex].selectedOption = questions[currentQuestionIndex].options[selectedOptionIndex]
                currentQuestionIndex++

                if (currentQuestionIndex < questions.size) {
                    displayQuestion(currentQuestionIndex)
                } else {
                    // User has completed all questions, show "Submit" button
                    nextButton.visibility = View.GONE
                    submitButton.visibility = View.VISIBLE
                }
                clearErrorMessage()
            } else {
                displayErrorMessage("Please select an option.")
            }
        }

        submitButton.setOnClickListener {
            val selectedOptionId = answerOptionsGroup.checkedRadioButtonId
            if (selectedOptionId == -1) {
                displayErrorMessage("Please select an option.")
            } else {
                val selectedOptionIndex = answerOptionsGroup.indexOfChild(findViewById(selectedOptionId))
                questions[currentQuestionIndex].selectedOption = questions[currentQuestionIndex].options[selectedOptionIndex]
                clearErrorMessage()

                // Calculate the number of correct answers
                val category = calculateCategory()

                // Navigate back to home screen
                navigateBackToHomeScreen(category)
            }
        }
    }

    private fun displayQuestion(index: Int) {
        val question = questions[index]
        questionTextView.text = "Question ${index + 1}: ${question.text}"
        // Clear previous selection and remove existing radio buttons
        answerOptionsGroup.clearCheck()
        answerOptionsGroup.removeAllViews()

        // Add new radio buttons based on the current question's options
        for ((optionText, _) in question.options) {
            val radioButton = RadioButton(this)
            radioButton.text = optionText
            radioButton.id = View.generateViewId()
            answerOptionsGroup.addView(radioButton)
        }

        if (currentQuestionIndex == questions.size - 1) {
            // On the last question, hide "Next" button and show "Submit" button
            nextButton.visibility = View.GONE
            submitButton.visibility = View.VISIBLE
        } else {
            // Not the last question, ensure "Submit" button is hidden
            nextButton.visibility = View.VISIBLE
            submitButton.visibility = View.GONE
        }
    }

    private fun displayErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }

    private fun clearErrorMessage() {
        errorMessageTextView.visibility = View.GONE
    }

    private fun calculateCategory(): String {
        val redCategory = "High"
        val amberCategory = "Mild"
        val greenCategory = "Low"

        // Collect categories for all questions
        val categories = questions.mapNotNull { question ->
            question.selectedOption?.second
        }

        return when {
            redCategory in categories -> redCategory
            amberCategory in categories -> amberCategory
            else -> greenCategory
        }
    }

    private fun navigateBackToHomeScreen(category: String) {
        val intent = Intent()
        intent.putExtra("completedTest", true)
        intent.putExtra("category", category) // Set user's category
        setResult(Activity.RESULT_OK, intent)
        Log.d("QuestionnaireActivity", "category: $category")
        finish()
    }
}


