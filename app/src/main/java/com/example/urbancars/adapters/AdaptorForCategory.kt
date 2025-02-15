package com.example.urbancars.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.urbancars.databinding.ItemviewProductBinding
import com.example.urbancars.models.Category
import java.util.ArrayList

class AdaptorForCategory(
    val categoryList: ArrayList<Category>, val onCategoryClick: (Category) -> Unit):RecyclerView.Adapter<AdaptorForCategory.CategoryViewHolder>() {
    class CategoryViewHolder(val binding:ItemviewProductBinding) :ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemviewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category=categoryList[position]
        holder.binding.apply {
            iv.setImageResource(category.image)
            tv.text=category.title
        }
        holder.itemView.setOnClickListener {
            onCategoryClick(category)
        }
    }

}
