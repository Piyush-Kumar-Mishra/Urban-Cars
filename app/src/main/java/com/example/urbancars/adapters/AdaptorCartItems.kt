package com.example.urbancars.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.urbancars.Room.CartItems
import com.example.urbancars.databinding.IvCartItemsBinding

class AdaptorCartItems: RecyclerView.Adapter<AdaptorCartItems.CartItemsViewHolder>(){
    class CartItemsViewHolder(val binding: IvCartItemsBinding): RecyclerView.ViewHolder(binding.root)

    val diffUtil=object:DiffUtil.ItemCallback<CartItems>(){
        override fun areItemsTheSame(oldItem: CartItems, newItem: CartItems): Boolean {
            return oldItem.ItemId==newItem.ItemId
        }

        override fun areContentsTheSame(oldItem: CartItems, newItem: CartItems): Boolean {
            return oldItem==newItem
        }
    }

    val differ= AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        return CartItemsViewHolder(IvCartItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val item=differ.currentList[position]
        holder.binding.apply {
           Glide.with(holder.itemView).load(item.ItemImages).into(ivProductImage)
            tvProductTitle.text=item.ItemName
            tvProductQuantity.text=item.ItemFuelType.toString()
            Count.text=item.ItemCount.toString()
            tvProductPrice.text=item.ItemPrice

        }
    }
}