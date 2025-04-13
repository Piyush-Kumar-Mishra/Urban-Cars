package com.example.urbancars.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.urbancars.CartInterface
import com.example.urbancars.CartInterfaceHolder
import com.example.urbancars.Room.CartItems
import com.example.urbancars.adapters.AdaptorCartItems
import com.example.urbancars.databinding.ActivityUsersBinding
import com.example.urbancars.databinding.ShowCartItemsBinding
import com.example.urbancars.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersActivity : AppCompatActivity(), CartInterface {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartItemsList: List<CartItems>
    private lateinit var cartItemsAdapter: AdaptorCartItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CartInterfaceHolder.cartInterface = this

        getItemCount()
        onCartClick()
        getAllCartItems()
        onToCartBtnClick()
    }

    private fun onToCartBtnClick() {
        binding.moveToCart.setOnClickListener {
            startActivity(Intent(this, PlaceOrderActivity::class.java))
        }
    }

    private fun getAllCartItems() {
        viewModel.getCartItems().observe(this) {
            cartItemsList = it
        }
    }

    // Fetch and display the current item count
    private fun getItemCount() {
        viewModel.fetchCartItemCount()
        viewModel.cartItemCount.observe(this) { count ->
            if (count > 0) {
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = count.toString()
            } else {
                binding.llCart.visibility = View.GONE
                binding.tvNumberOfProductCount.text = "0"
            }
        }
    }

    override fun showCartUI(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toIntOrNull() ?: 0
        val updatedCount = previousCount + itemCount
        if (updatedCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }


    override fun saveItemCount(itemCount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentCount = viewModel.cartItemCount.value ?: 0
            val newCount = currentCount + itemCount
            withContext(Dispatchers.Main) {
                viewModel.saveItemCount(itemCount)
                viewModel.fetchCartItemCount()
            }
        }
    }



    private fun onCartClick() {
        binding.llItemCart.setOnClickListener {
            val showcartItems = ShowCartItemsBinding.inflate(LayoutInflater.from(this))
            val showCart = BottomSheetDialog(this)
            showCart.setContentView(showcartItems.root)
            showcartItems.btnNext.setOnClickListener {
                startActivity(Intent(this, PlaceOrderActivity::class.java))
            }
            showcartItems.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text
            cartItemsAdapter = AdaptorCartItems()
            showcartItems.rvProductItems.adapter = cartItemsAdapter
            cartItemsAdapter.differ.submitList(cartItemsList)
            showCart.show()
        }
    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text = "0"
    }
}