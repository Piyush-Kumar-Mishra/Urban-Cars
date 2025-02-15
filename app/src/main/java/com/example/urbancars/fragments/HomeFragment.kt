package com.example.urbancars.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.urbancars.Constants
import com.example.urbancars.R
import com.example.urbancars.adapters.AdaptorForCategory
import com.example.urbancars.models.Category
import com.example.urbancars.databinding.FragmentHomeBinding
import com.example.urbancars.viewmodels.UserViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        changeStatusBarColor()
        setAllCategories()
        navigateToSearchFragent()
        get()
        return binding.root
    }

    private fun setAllCategories() {
        val categoryList=ArrayList<Category>()

        for(i in 0 until Constants.allProdductCategory.size){
            categoryList.add(Category(
                Constants.allProdductCategory[i],
                Constants.allProductCategoryImage[i]))
        }
        binding.rvCategories.adapter= AdaptorForCategory(categoryList,::onCategoryClick)
    }

    private fun navigateToSearchFragent(){
        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun changeStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun onCategoryClick(category: Category){
        val bundle=Bundle()
        bundle.putString("category",category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment,bundle)
    }

    private fun get(){
        viewModel.getCartItems().observe(viewLifecycleOwner){
            for(i in it){
                Log.d("a",i.ItemCompany.toString())
                Log.d("a",i.ItemCount.toString())
            }
        }
    }

}