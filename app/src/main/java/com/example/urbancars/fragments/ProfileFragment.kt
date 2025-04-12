package com.example.urbancars.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.urbancars.R
import com.example.urbancars.Utils
import com.example.urbancars.activity.AuthMainActivity
import com.example.urbancars.databinding.FragmentProfileBinding
import com.example.urbancars.databinding.ShowAddressBinding
import com.example.urbancars.viewmodels.UserViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        onBackbutton()
        onOrderClick()
        onAddressClick()
        onLogOutClick()
        return binding.root
    }
    private fun onLogOutClick() {
        binding.LogOut.setOnClickListener {

            val builder= AlertDialog.Builder(requireContext())
            val alertDialog= builder.create()
                builder.setTitle("Loging Out").setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.logout()
                    startActivity(Intent(requireContext(), AuthMainActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No") { _, _ ->
                    alertDialog.dismiss()
                }
                    .show()
                    .setCancelable(false)
        }
    }
    private fun onAddressClick() {
        binding.Address.setOnClickListener{
            val addressLayoutBinding=ShowAddressBinding.inflate(LayoutInflater.from(requireContext()))
                viewModel.getUserAddress { address->
                    addressLayoutBinding.etUserAddress.setText(address.toString())
                }

            val alertDialog=AlertDialog.Builder(requireContext()).setView(addressLayoutBinding.root).create()
            alertDialog.show()

            addressLayoutBinding.SaveAddress.setOnClickListener {
                    viewModel.saveAddress(addressLayoutBinding.etUserAddress.text.toString())
                    alertDialog.dismiss()
                    Utils.showToast(requireContext(),"Address Saved")
            }
            addressLayoutBinding.EditAddress.setOnClickListener {
                addressLayoutBinding.etUserAddress.isEnabled=true
            }

        }
    }

    private fun onOrderClick() {
        binding.llOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackbutton() {
        binding.tbProfile.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }
}
