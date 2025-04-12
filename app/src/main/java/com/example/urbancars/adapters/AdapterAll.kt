package com.example.urbancars.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.urbancars.databinding.IvAllBinding
import com.example.urbancars.models.All

class AdapterAll(val onSeeAllClick: (All) -> Unit) :RecyclerView.Adapter<AdapterAll.AllViewHolder>() {
    class AllViewHolder (val binding:IvAllBinding): RecyclerView.ViewHolder(binding.root){

    }
val diffUtil=object :DiffUtil.ItemCallback<All>(){
    override fun areItemsTheSame(oldItem: All, newItem: All): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: All, newItem: All): Boolean {
        return oldItem==newItem
    }

}
    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {
            return AllViewHolder(IvAllBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AllViewHolder, position: Int) {
        val itemType=differ.currentList[position]
        holder.binding.apply {
            tvCategory.text=itemType.itemType
            tvTotalItems.text=itemType.items?.size.toString() + " Items"

            val listOfIv=listOf(ivProduct1,ivProduct2,ivProduct3)
            val minimumSize=minOf(listOfIv.size,itemType.items?.size!!)
            for(i in 0 until minimumSize){
                listOfIv[i].visibility=android.view.View.VISIBLE
                Glide.with(holder.itemView).load(itemType.items[i].ItemImagesUris?.get(3)).into(listOfIv[i])
            }
            if(itemType.items?.size!! > 3){
                tvProductCount.visibility= View.VISIBLE
                tvProductCount.text="+" + (itemType.items.size-3).toString()
            }
        }

        holder.itemView.setOnClickListener{
            onSeeAllClick(itemType)
        }
    }
}