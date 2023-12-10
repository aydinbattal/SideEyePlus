package sheridan.czuberad.sideeye.Adapters

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-26 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sheridan.czuberad.sideeye.Domain.Questionnaire
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.R

class CombinedResultAdapter(private val combinedResults: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View types for different item types
    private val REACTION_TEST_VIEW_TYPE = 1
    private val QUESTIONNAIRE_RESULT_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            REACTION_TEST_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_reaction_test, parent, false)
                ReactionTestViewHolder(view)
            }
            QUESTIONNAIRE_RESULT_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_questionnaire, parent, false)
                QuestionnaireResultViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = combinedResults[position]
        when (holder) {
            is ReactionTestViewHolder -> {
                val reactionTest = item as ReactionTest
                holder.bindReactionTest(reactionTest)
            }
            is QuestionnaireResultViewHolder -> {
                val questionnaireResult = item as Questionnaire
                holder.bindQuestionnaireResult(questionnaireResult)
            }
        }
    }

    override fun getItemCount(): Int {
        return combinedResults.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = combinedResults[position]
        return when (item) {
            is ReactionTest -> REACTION_TEST_VIEW_TYPE
            is Questionnaire -> QUESTIONNAIRE_RESULT_VIEW_TYPE
            else -> throw IllegalArgumentException("Invalid item type: ${item.javaClass.simpleName}")
        }
    }

    class ReactionTestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for ReactionTest item
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)

        fun bindReactionTest(reactionTest: ReactionTest) {
            // Bind data for ReactionTest
            dateTextView.text = "${reactionTest.date}"
            scoreTextView.text = "Average Reaction Time: ${reactionTest.avgTime}"
        }
    }

    class QuestionnaireResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for QuestionnaireResult item
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)

        fun bindQuestionnaireResult(questionnaireResult: Questionnaire) {
            // Bind data for QuestionnaireResult
            dateTextView.text = "${questionnaireResult.date}"
            categoryTextView.text = "Category: ${questionnaireResult.category}"
        }
    }
}

