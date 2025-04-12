package com.example.urbancars.fragments

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
import com.example.urbancars.R
import com.example.urbancars.adapters.AdaptorCartItems
import com.example.urbancars.databinding.FragmentOrderDetailsBinding
import com.example.urbancars.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {
    private lateinit var binding : FragmentOrderDetailsBinding
    private val viewModel:UserViewModel by viewModels()
    private lateinit var adapter:AdaptorCartItems
    private var status=0
    private var orderId=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrderDetailsBinding.inflate(layoutInflater)
        getValues()
        settingStatus()
        lifecycleScope.launch {
            getOrderedItems()
        }
        onBackBtnClick()
        return binding.root
    }

    suspend fun getOrderedItems() {
        viewModel.getOrderedItems(orderId).collect{ cartList->
            adapter=AdaptorCartItems()
            binding.rvProductItems.adapter=adapter
            adapter.differ.submitList(cartList)

        }
    }

    private fun getValues(){
        val bundle=arguments
        status=bundle?.getString("status")?.toIntOrNull() ?: 0
        orderId=bundle?.getString("orderId").toString()
        Log.d("OrderDetails", "Received status: $status, orderId: $orderId")
    }

    private fun settingStatus() {
        Log.d("OrderDetails", "Setting status views for status = $status")

        val statusToViews = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2),
            3 to listOf(
                binding.iv1,
                binding.iv2,
                binding.view1,
                binding.iv3,
                binding.view2,
                binding.iv4,
                binding.view3
            )
        )

        val viewsToTint = statusToViews.getOrDefault(status, emptyList())
        for (view in viewsToTint) {
            view.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }
    }

    private fun onBackBtnClick(){
        binding. tbOrderDetailFragment . setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailsFragment_to_ordersFragment)
        }
    }
}
