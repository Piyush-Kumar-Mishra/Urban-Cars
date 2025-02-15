package com.example.urbancars.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.urbancars.R
import com.example.urbancars.activity.UsersActivity
import com.example.urbancars.databinding.FragmentSplashBinding
import com.example.urbancars.viewmodels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        changeStatusBarColor()

        lifecycleScope.launch {
            delay(3000)
            val isSignedIn = viewModel.alreadySignedIn.first()
            if (isSignedIn) {
                val intent = Intent(requireContext(), UsersActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
            }
        }
        return binding.root
    }

    private fun changeStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
