package sheridan.czuberad.sideeye

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.databinding.CustomRowLayoutBinding

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2022-11-19 */
class DriversAdapter : ListAdapter<Driver, DriversAdapter.DriversViewHolder>(CompanyDriversAdapter()) {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    inner class DriversViewHolder(val binding: CustomRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                mListener.onItemClick(adapterPosition)
            }
        }
    }

    class CompanyDriversAdapter : DiffUtil.ItemCallback<Driver>() {
        override fun areItemsTheSame(oldItem: Driver, newItem: Driver): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Driver, newItem: Driver): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CustomRowLayoutBinding.inflate(layoutInflater, parent, false)
        return DriversViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DriversViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.tvDriverName.text = item.name.toString()
        holder.binding.tvDriverStatus.text = item.status

        when (item.status) {
            "Low" -> holder.binding.tvDriverStatus.setTextColor(Color.GREEN)
            "Mild" -> holder.binding.tvDriverStatus.setTextColor(Color.rgb(252, 88, 5))
            else -> holder.binding.tvDriverStatus.setTextColor(Color.RED)
        }
    }
}
