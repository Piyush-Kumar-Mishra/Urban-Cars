package com.example.urbancars.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.urbancars.R
import com.example.urbancars.databinding.IvOrderBinding
import com.example.urbancars.models.OrderedItems

class OrderAdaptor(val requireContext: Context, val OrderItemView: (OrderedItems) -> Unit) : RecyclerView.Adapter<OrderAdaptor.OrderViewHolder>() {

    class OrderViewHolder(val binding: IvOrderBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(IvOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        Log.d("OrderAdaptor", "Binding order: ${order.orderId}")
        holder.binding.apply {
            tvOrderTitle.text = order.itemTitle
            tvOrderDate.text = order.itemDate
            tvOrderAmount.text = "₹${order.itemPrice}"

            when (order.itemStatus) {
                0.toString() -> setStatus(holder, "Ordered", R.color.yellow)
                1.toString() -> setStatus(holder, "Processing", R.color.blue)
                2.toString() -> setStatus(holder, "Shipped", R.color.appbcg2)
                3.toString() -> setStatus(holder, "Delivered", R.color.appbcg)
                else -> setStatus(holder, "Unknown", R.color.black)
            }
        }
        holder.itemView.setOnClickListener {
            OrderItemView(order)
        }
    }

    private fun setStatus(holder: OrderViewHolder, statusText: String, color: Int) {
        holder.binding.tvOrderStatus.text = statusText
        holder.binding.tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, color)
    }
}
