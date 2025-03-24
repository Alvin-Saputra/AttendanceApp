package com.example.attendanceapp.ui.success

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.FragmentSelfieBinding
import com.example.attendanceapp.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_success, container, false)
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvSuccess.alpha = 0f
        binding.btnBackToHome.alpha = 0f

        Handler(Looper.getMainLooper()).postDelayed({
            playAnimation()
        }, 1500)

        binding.btnBackToHome.setOnClickListener{
            findNavController().navigate(R.id.action_successFragment_to_navigation_home)
        }

        return root
    }


    companion object {

    }

    private fun playAnimation() {



        val tvSuccessAnim =
            ObjectAnimator.ofFloat(binding.tvSuccess, View.ALPHA, 1f).setDuration(400)

        val btnBackToHomeAnim =
            ObjectAnimator.ofFloat(binding.btnBackToHome, View.ALPHA, 1f).setDuration(400)


        AnimatorSet().apply {
            playSequentially(
               tvSuccessAnim,
                btnBackToHomeAnim
            )
            start()

        }
    }
}