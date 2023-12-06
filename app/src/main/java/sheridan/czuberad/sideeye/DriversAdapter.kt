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
class DriversAdapter : ListAdapter<Driver, DriversAdapter.DriversViewHolder>(DriversAdapter.CompanyDriversAdapter()) {

    //var onItemClick : ((Driver) -> Unit)? = null
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    inner class DriversViewHolder(val binding: CustomRowLayoutBinding, clickListener: onItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    class CompanyDriversAdapter: DiffUtil.ItemCallback<Driver>() {
        override fun areItemsTheSame(oldItem: Driver, newItem: Driver): Boolean {
            TODO("Implement this")
            // return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Driver, newItem: Driver): Boolean {
            TODO("Implement this")
            // return oldItem == newItem
        }
    }

    // mandatory functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversAdapter.DriversViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CustomRowLayoutBinding.inflate(layoutInflater, parent, false)
        return DriversViewHolder(binding, mListener)
    }

    // specify what data should be placed in each UI element of the custom row layout
    override fun onBindViewHolder(holder: DriversAdapter.DriversViewHolder, position: Int) {
//        val driver : Driver = driversList[position]
        Log.d("ABC", "onBindViewHolder is called!")
        val item = getItem(position)
        Log.d("adapter", item.name.toString())

        holder.binding.tvDriverName.text = item.name.toString()
        holder.binding.tvDriverStatus.text = item.status
        if(item.status == "Low"){
            holder.binding.tvDriverStatus.setTextColor(Color.GREEN)
        } else if(item.status == "Mild") {
            holder.binding.tvDriverStatus.setTextColor(Color.rgb(252, 88, 5))
        } else {
            holder.binding.tvDriverStatus.setTextColor(Color.RED)
        }


//        holder.itemView.setOnClickListener{
//            onItemClick?.invoke(item)
//        }

    }

//    override fun getItemCount(): Int {
//        return driversList.size
//    }

//    class DriversViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val name : TextView = itemView.findViewById(R.id.tvDriverName)
//    }


}