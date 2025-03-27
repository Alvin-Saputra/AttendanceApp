//package com.example.attendanceapp.ui.login
//
//import android.content.Intent
//import androidx.fragment.app.viewModels
//import android.os.Bundle
//import android.util.Log
//import android.util.Patterns
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import com.example.attendanceapp.R
//import com.example.attendanceapp.data.pref.UserModel
//import com.example.attendanceapp.databinding.FragmentLoginBinding
//import com.example.attendanceapp.databinding.FragmentSelfieBinding
//import com.example.attendanceapp.ui.login.ViewModelFactory
//import com.example.attendanceapp.repository.Result
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class LoginFragment : Fragment() {
//
//    private var _binding: FragmentLoginBinding? = null
//    private val binding get() = _binding!!
//
//    private val viewModel: LoginViewModel by viewModels() {
//        ViewModelFactory(requireContext())
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
////        return inflater.inflate(R.layout.fragment_login, container, false)
//        _binding = FragmentLoginBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        binding.progressBarLogin.visibility = View.GONE
//
//        binding.buttonLogin.setOnClickListener{
//
//                var userId = binding.textInputEditTextUserId.text.toString()
//                var password = binding.textInputEditTextPassword.text.toString()
//
//                if (binding.textInputEditTextUserId.text.toString().isEmpty() ||
//                    binding.textInputEditTextPassword.text.toString().isEmpty()
//                ) {
//                    showToast("All field must be filled")
//                }
//                 else {
//                    viewModel.loginRequest(userId, password).observe(viewLifecycleOwner) { result ->
//                        when (result) {
//                            is Result.Loading -> {
//                                Log.d("LoginActivity", "Loading stories...")
//                                binding.progressBarLogin.visibility = View.VISIBLE
//                            }
//                            is Result.Success -> {
//                                binding.progressBarLogin.visibility = View.GONE
//                                val logindata = result.data
//                                saveSession(logindata.username, logindata.userId)
//                            }
//
//                            is Result.Error -> {
//                                binding.progressBarLogin.visibility = View.GONE
//                                Log.e("LoginActivity", "Error: ${result.error}")
//                                showToast(result.error)
//                            }
//                        }
//                    }
//                }
//
//
//        }
//
//        return root
//    }
//
//
//    private fun saveSession(username: String, userId: String) {
//        lifecycleScope.launch {
//            viewModel.saveSession(
//                UserModel(
//                    username = username,
//                    userId = userId,
//                    isLogin = true
//                )
//            )
//
//            delay(1000)
//            withContext(Dispatchers.Main) {
//               findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
//            }
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }
//}