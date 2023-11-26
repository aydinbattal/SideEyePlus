package sheridan.czuberad.sideeye

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-26 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sheridan.czuberad.sideeye.Domain.ReactionTest

class ReactionTestAdapter(private val reactionTestResults: List<ReactionTest>) :
    RecyclerView.Adapter<ReactionTestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reaction_test, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reactionTest = reactionTestResults[position]
        holder.bind(reactionTest)
    }

    override fun getItemCount(): Int {
        return reactionTestResults.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)

        fun bind(reactionTest: ReactionTest) {
            dateTextView.text = "Date: ${reactionTest.date}"
            scoreTextView.text = "Score: ${reactionTest.avgTime}"
        }
    }
}
