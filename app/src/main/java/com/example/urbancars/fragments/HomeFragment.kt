package com.example.urbancars.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.urbancars.CartInterface
import com.example.urbancars.Constants
import com.example.urbancars.R
import com.example.urbancars.Room.CartItems
import com.example.urbancars.Utils
import com.example.urbancars.adapters.AdapterAll
import com.example.urbancars.adapters.AdaptorForCategory
import com.example.urbancars.adapters.ProductAdaptor
import com.example.urbancars.models.Category
import com.example.urbancars.databinding.FragmentHomeBinding
import com.example.urbancars.databinding.IvItemsBinding
import com.example.urbancars.databinding.SeeAllBinding
import com.example.urbancars.models.All
import com.example.urbancars.models.Item
import com.example.urbancars.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterAll: AdapterAll
    private lateinit var adapterItem: ProductAdaptor
    private var cartInterface: CartInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        changeStatusBarColor()
        setAllCategories()
        navigateToSearchFragment()
        get()
        onProfile()
        fetchAll()
        return binding.root
    }

    fun onSeeAllClick(productType: All) {
        val seeAllBinding=SeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs=BottomSheetDialog(requireContext())
        bs.setContentView(seeAllBinding.root)
        adapterItem= ProductAdaptor(::onAddToCart,::onCartIncrement, ::onCartDecrement )
        seeAllBinding.rvProducts.adapter=adapterItem
        adapterItem.differ.submitList(productType.items)
        bs.show()
    }

    private fun fetchAll() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getItemTypes().collect{
                adapterAll=AdapterAll(::onSeeAllClick)
                binding.rvBestselers.adapter=adapterAll
                adapterAll.differ.submitList(it)
                binding.shimmerViewContainer.visibility = View.GONE

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start flickering animation for wheel
        startFlickeringLight(binding.wheel)
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in Constants.allProdductCategory.indices) {
            categoryList.add(
                Category(
                    Constants.allProdductCategory[i],
                    Constants.allProductCategoryImage[i]
                )
            )
        }
        binding.rvCategories.adapter = AdaptorForCategory(categoryList, ::onCategoryClick)
    }

    private fun navigateToSearchFragment() {
        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun changeStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbcg3)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun onCategoryClick(category: Category) {
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    private fun get() {
        viewModel.getCartItems().observe(viewLifecycleOwner) {
            for (i in it) {
                Log.d("a", i.ItemCompany.toString())
                Log.d("a", i.ItemCount.toString())
            }
        }
    }

    private fun onProfile(){
        binding.ivProfile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }


    fun startFlickeringLight(view: View, duration: Long = 5000L) {
        val flickerDurations = listOf(400L, 600L, 600L, 600L)

        val animations = mutableListOf<ObjectAnimator>()

        for (time in flickerDurations) {
            animations.add(ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(time))
            animations.add(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(time))
        }

        val flickerSet = AnimatorSet()
        flickerSet.playSequentially(animations as List<Animator>?)
        flickerSet.start()
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
            itemInStock = product.itemInStock,
            ItemCount = product.itemCount ?: 0
        )

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.insertCartProduct(cartItem)
        }
    }

    private fun onCartIncrement(item: Item, productBinding: IvItemsBinding) {
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(item.itemInStock!! +1 >itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()
            cartInterface?.showCartUI(1)

            item.itemCount = itemCountInc
            lifecycleScope.launch(Dispatchers.IO) {
                cartInterface?.saveItemCount(1)
                saveItemInRoom(item)
                viewModel.updateItemCount(item, itemCountInc)
            }
        }
        else{
            Utils.showToast(requireContext(),"Out of Stock")
        }

    }

    private fun onCartDecrement(item: Item, productBinding: IvItemsBinding) {
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--
        item.itemCount = itemCountDec

        lifecycleScope.launch(Dispatchers.IO) {
            cartInterface?.saveItemCount(-1)
            saveItemInRoom(item)
            viewModel.updateItemCount(item, itemCountDec)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.deleteCartItem(item.ItemRandomId)
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




    private fun onAddToCart(item: Item, productBinding: IvItemsBinding) {
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
            viewModel.updateItemCount(item, itemCount)
        }

    }

}