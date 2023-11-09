package sheridan.czuberad.sideeye

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import sheridan.czuberad.sideeye.databinding.ActivityQuestionnaireBinding
import sheridan.czuberad.sideeye.databinding.ActivityReactionTestBinding

class QuestionnaireActivity : AppCompatActivity() {
    private lateinit var questionTextView: TextView
    private lateinit var answerOptionsGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button
    private lateinit var errorMessageTextView: TextView

    private lateinit var binding: ActivityQuestionnaireBinding

    private data class Question(val text: String, var userAnswerId: Int = -1, var correctAnswerId: Int = -1)

    private val questions = mutableListOf(
        Question("Are you feeling drowsy?", -1, R.id.option1),
        Question("Have you had enough rest?", -1, R.id.option2),
        // Add more questions here with correct answer IDs
    )

    private var currentQuestionIndex = 0
    private var correctAnswers = 0

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
                questions[currentQuestionIndex].userAnswerId = selectedOptionId // Store the answer
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
                questions[currentQuestionIndex].userAnswerId = selectedOptionId // Store the answer for the last question
                clearErrorMessage()

                // Calculate the number of correct answers
                calculateCorrectAnswers()

                // Navigate back to home screen
                navigateBackToHomeScreen()
            }
        }
    }

    private fun displayQuestion(index: Int) {
        val question = questions[index]
        questionTextView.text = "Question ${index + 1}: ${question.text}"
        // Set radio button options for the current question
        answerOptionsGroup.clearCheck() // Clear previous selection

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

    private fun calculateCorrectAnswers() {
        for (question in questions) {
            if (question.userAnswerId == question.correctAnswerId) {
                correctAnswers++
            }
        }
    }

    private fun navigateBackToHomeScreen() {
        val intent = Intent()
        intent.putExtra("completedTest", true) // Set the boolean variable
        val score = "$correctAnswers/${questions.size}"
        intent.putExtra("score", score) // Set the number of correct answers
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}


