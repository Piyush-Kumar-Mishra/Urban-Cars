package com.example.urbancars.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.urbancars.CartInterface
import com.example.urbancars.databinding.ActivityUsersBinding
import com.example.urbancars.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersActivity : AppCompatActivity(), CartInterface {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getItemCount()
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
            }
        }
    }

    // Update the cart UI when an item is added or removed
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

    // Save the updated item count to SharedPreferences
    override fun saveItemCount(itemCount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentCount = viewModel.cartItemCount.value ?: 0
            val newCount = currentCount + itemCount
            withContext(Dispatchers.Main) {
                viewModel.saveItemCount(itemCount) // Save the difference
                viewModel.fetchCartItemCount() // Update LiveData
            }
        }
    }
}