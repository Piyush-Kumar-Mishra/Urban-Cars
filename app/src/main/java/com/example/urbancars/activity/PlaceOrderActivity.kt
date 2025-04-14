package com.example.urbancars.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.urbancars.CartInterface
import com.example.urbancars.CartInterfaceHolder
import com.example.urbancars.R
import com.example.urbancars.Utils
import com.example.urbancars.adapters.AdaptorCartItems
import com.example.urbancars.databinding.ActivityPlaceOrderBinding
import com.example.urbancars.databinding.FillAddressLayoutBinding
import com.example.urbancars.models.Order
import com.example.urbancars.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class PlaceOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceOrderBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartItemsAdapter: AdaptorCartItems
    private var cartInterface: CartInterface? = null

    companion object {
        private const val FREE_DELIVERY_THRESHOLD = 50000
        private const val DELIVERY_CHARGE = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartInterface = CartInterfaceHolder.cartInterface

        cartItemsAdapter = AdaptorCartItems()
        binding.rvProductsItem.adapter = cartItemsAdapter
        changeStatusBarColor()
        getAllCartItems()
        onBackClick()
        saveOrder()
        onPlaceOrderClick()
    }

    private fun onBackClick() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            finish()
        }
    }

    private fun getAllCartItems() {
        viewModel.getCartItems().observe(this) { cartItemsList ->
            cartItemsAdapter.differ.submitList(cartItemsList)

            var totalAmount = 0
            for (items in cartItemsList) {
                val amount = items.ItemPrice.substring(1).toIntOrNull() ?: 0
                val itemCount = items.ItemCount
                totalAmount += amount * itemCount!!
            }

            binding.tvSubTotal.text = "₹$totalAmount"
            if (totalAmount < FREE_DELIVERY_THRESHOLD) {
                binding.tvDeliveryCharge.text = "₹$DELIVERY_CHARGE"
                totalAmount += DELIVERY_CHARGE
            } else {
                binding.tvDeliveryCharge.text = "Free"
            }
            binding.tvFinalTotal.text = "₹$totalAmount"
        }
    }

    private fun saveAddress(
        alertDialog: AlertDialog,
        fillAddressLayoutBinding: FillAddressLayoutBinding
    ) {
        val address =
            "${fillAddressLayoutBinding.etAddress.text}, ${fillAddressLayoutBinding.etDistrict.text} (${fillAddressLayoutBinding.etPinCode.text}), ${fillAddressLayoutBinding.etState.text}, (${fillAddressLayoutBinding.etPhoneNo.text})"
        lifecycleScope.launch {
            viewModel.savingAddressInFB(address)
            viewModel.saveAddressStatus(true)
        }
        alertDialog.dismiss()
        Utils.showToast(this, "Address saved")
    }

    private fun changeStatusBarColor() {
        window?.apply {
            statusBarColor = ContextCompat.getColor(this@PlaceOrderActivity, R.color.black)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun onPlaceOrderClick() {
        binding.btnNext.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status) {
                    saveOrder()
                    viewModel.deleteCartItems()
                    viewModel.saveItemCount(0)
                    cartInterface?.hideCartLayout() // Hide cart UI
                    Utils.showToast(this, "Order placed successfully!")
                } else {
                    val fillAddressLayoutBinding =
                        FillAddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog =
                        AlertDialog.Builder(this).setView(fillAddressLayoutBinding.root).create()
                    alertDialog.show()

                    fillAddressLayoutBinding.btnAddAddress.setOnClickListener {
                        saveAddress(alertDialog, fillAddressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveOrder() {
        viewModel.getCartItems().observe(this) { cartItemsList ->
            Log.d("OrderDebug", "Cart items to save: ${cartItemsList?.size} items")
            if (cartItemsList.isNullOrEmpty()) {
                Log.e("OrderError", "No cart items to save!")
                return@observe
            }

            viewModel.getUserAddress { address ->
                val order = Order(
                    orderId = Utils.getRandomId(),
                    orderList = cartItemsList,
                    userAddress = address,
                    orderStatus = 0,
                    orderDate = Utils.getCurrentDate(),
                    orderingUserId = Utils.getCurrentUserId()
                )
                Log.d("OrderDebug", "Order to save: $order")
                viewModel.saveOrderedProducts(order)
            }
        }
    }
}

