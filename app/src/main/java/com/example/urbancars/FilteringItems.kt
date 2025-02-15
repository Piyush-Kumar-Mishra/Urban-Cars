package com.example.urbancars


import android.widget.Filter
import com.example.urbancars.adapters.ProductAdaptor
import com.example.urbancars.models.Item
import java.util.Locale

class FilteringItems (
    val adapter: ProductAdaptor,
    val filter:ArrayList<Item>
): Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()
        if (!constraint.isNullOrEmpty()) {
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            val filteredList = ArrayList<Item>()
            for (item in filter) {
                if (query.any {
                        item.ItemName?.uppercase(Locale.getDefault())?.contains(it) == true ||
                        item.ItemCompany?.uppercase(Locale.getDefault())?.contains(it)==true
                    }) {
                    filteredList.add(item)
                }
            }
            result.values = filteredList
            result.count = filteredList.size

        }
        else{
            result.values=filter
            result.count=filter.size
        }
        return result
    }
    override fun publishResults(p0: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as ArrayList<Item>)
    }

}