package com.example.urbancars.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.urbancars.CartInterface
import com.example.urbancars.R
import com.example.urbancars.Room.CartItems
import com.example.urbancars.adapters.ProductAdaptor
import com.example.urbancars.databinding.FragmentCategoryBinding
import com.example.urbancars.databinding.IvProductsBinding
import com.example.urbancars.models.Item
import com.example.urbancars.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var category: String? = null
    private val viewModel: UserViewModel by viewModels()
    private lateinit var productAdaptor: ProductAdaptor
    private var cartInterface: CartInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        getItemCategory()
        toolBartitle()
        fetchCategoryProducts()
        searchbtnOnMenu()
        backbtnOnMenu()
        return binding.root
    }

    private fun backbtnOnMenu() {
        binding.tbSearch.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun searchbtnOnMenu() {
        binding.tbSearch.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuSearch -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun getItemCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }

    private fun toolBartitle() {
        binding.tbSearch.title = category
    }

    private fun fetchCategoryProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getProductFromCategory(category!!).collect { products ->
                withContext(Dispatchers.Main) {
                    if (products.isEmpty()) {
                        binding.rvProducts.visibility = View.GONE
                        binding.tvText.visibility = View.VISIBLE
                    } else {
                        binding.rvProducts.visibility = View.VISIBLE
                        binding.tvText.visibility = View.GONE
                    }

                    productAdaptor = ProductAdaptor(::onAddToCart, ::onCartIncrement, ::onCartDecrement)
                    binding.rvProducts.adapter = productAdaptor
                    productAdaptor.differ.submitList(products)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }

        }
    }

    private fun onAddToCart(item: Item, productBinding: IvProductsBinding) {
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()
        cartInterface?.showCartUI(1)

        item.itemCount = itemCount
        lifecycleScope.launch(Dispatchers.IO) {
            cartInterface?.saveItemCount(1)
            saveItemInRoom(item)
        }
    }

    private fun saveItemInRoom(product: Item) {
        val cartItem = CartItems(
            ItemName = product.ItemName!!,
            ItemCompany = product.ItemCompany!!,
            ItemYear = product.ItemYear!!,
            ItemPrice = "₹${product.ItemPrice}",
            ItemId = product.ItemRandomId,
            ItemFuelType = product.ItemFuelType!!,
            ItemDistanceCovered = product.ItemDistanceCovered!!,
            ItemOtherDetails = product.ItemOtherDetails!!,
            ItemImages = product.ItemImagesUris?.get(0)!!,
            AdminUid = product.AdminUid!!,
            ItemCount = product.itemCount
        )

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.insertCartProduct(cartItem)
        }
    }

    private fun onCartIncrement(item: Item, productBinding: IvProductsBinding) {
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++
        productBinding.tvProductCount.text = itemCountInc.toString()
        cartInterface?.showCartUI(1)

        item.itemCount = itemCountInc
        lifecycleScope.launch(Dispatchers.IO) {
            cartInterface?.saveItemCount(1)
            saveItemInRoom(item)
        }
    }

    private fun onCartDecrement(item: Item, productBinding: IvProductsBinding) {
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--
        item.itemCount = itemCountDec

        lifecycleScope.launch(Dispatchers.IO) {
            cartInterface?.saveItemCount(-1)
            saveItemInRoom(item)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.deleteCartItem(item.ItemRandomId!!)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }
        cartInterface?.showCartUI(-1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartInterface) {
            cartInterface = context
        } else {
            throw ClassCastException("$context must implement CartInterface")
        }
    }
}
