package com.example.urbancars.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.urbancars.R
import com.example.urbancars.adapters.OrderAdaptor
import com.example.urbancars.databinding.FragmentOrdersBinding
import com.example.urbancars.models.OrderedItems
import com.example.urbancars.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterOrders: OrderAdaptor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        adapterOrders = OrderAdaptor(requireContext(), ::OrderItemView)
        binding.rvOrders.adapter = adapterOrders
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        onBackClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllOrders()
    }

    private fun getAllOrders() {
        binding.shimmerViewContainerOrders.visibility = View.VISIBLE
        binding.tvText.visibility = View.GONE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect { orderList ->
                Log.d("OrdersFragment", "Fetched orders: ${orderList.size}")
                if (!orderList.isNullOrEmpty()) {
                    val orderedList = ArrayList<OrderedItems>()
                    for (orders in orderList) {
                        val title = StringBuilder()
                        var totalAmount = 0

                        if (!orders.orderList.isNullOrEmpty()) {
                            for (items in orders.orderList!!) {
                                val amount = items.ItemPrice?.substring(1)?.toIntOrNull() ?: 0
                                val itemCount = items.ItemCount ?: 0
                                totalAmount += amount * itemCount
                                title.append("${items.ItemCompany} ")
                            }
                        } else {
                            title.append("No items in this order")
                        }

                        val orderedItems = OrderedItems(
                            orderId = orders.orderId ?: "Unknown",
                            itemTitle = title.toString(),
                            itemPrice = totalAmount.toString(),
                            itemDate = orders.orderDate ?: "Unknown Date",
                            itemStatus = orders.orderStatus?.toString() ?: "0",
                            userAddress = orders.userAddress ?: "Unknown Address"
                        )
                        orderedList.add(orderedItems)
                    }
                    Log.d("OrdersFragment", "Transformed orderedList: ${orderedList.size}")
                    adapterOrders.differ.submitList(orderedList)

                    binding.shimmerViewContainerOrders.visibility = View.GONE
                    binding.tvText.visibility = if (orderedList.isEmpty()) View.VISIBLE else View.GONE
                    binding.rvOrders.visibility = if (orderedList.isEmpty()) View.GONE else View.VISIBLE
                } else {
                    Log.d("OrdersFragment", "No orders found")
                    binding.rvOrders.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
            }
        }
    }

    fun OrderItemView(orderedItems: OrderedItems) {
        val bundle = Bundle()
        bundle.putString("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId!!)
        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailsFragment, bundle)
    }

    private fun onBackClick() {
        binding.tborder.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }

}
