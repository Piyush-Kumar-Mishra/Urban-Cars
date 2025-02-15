package com.example.urbancars.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.urbancars.FilteringItems
import com.example.urbancars.databinding.IvProductsBinding
import com.example.urbancars.models.Item

class ProductAdaptor(
    val onAddToCart: (Item, IvProductsBinding) -> Unit,
    val onCartIncrement: (Item, IvProductsBinding) -> Unit,
    val onCartDecrement: (Item, IvProductsBinding) -> Unit
) : RecyclerView.Adapter<ProductAdaptor.ProductViewHolder>(),Filterable {

    class ProductViewHolder(val binding: IvProductsBinding) : ViewHolder(binding.root)

    val diffutil = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.ItemRandomId == newItem.ItemRandomId
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffutil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            IvProductsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            val productImages = product.ItemImagesUris

            for (i in 0 until productImages?.size!!) {
                imageList.add(SlideModel(product.ItemImagesUris!![i].toString()))
            }
            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.ItemName
            tvProductQuantity.text = product.ItemFuelType
            tvProductPrice.text = "₹" + product.ItemPrice.toString()

            tvAdd.setOnClickListener{
                onAddToCart(product,this)
            }
            tvIncrementCount.setOnClickListener{
                onCartIncrement(product,this)
            }
            tvDecrementCount.setOnClickListener{
                onCartDecrement(product,this)
            }
        }

    }


    var originallist = ArrayList<Item>()
    private var filterInstance: FilteringItems? = null

    override fun getFilter(): Filter {
        if (filterInstance == null) {
            filterInstance = FilteringItems(this, originallist)
        }
        return filterInstance!!
    }
}
