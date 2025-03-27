package com.example.attendanceapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.attendanceapp.R
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.databinding.ActivityLoginBinding
import com.example.attendanceapp.databinding.FragmentLoginBinding
import com.example.attendanceapp.repository.Result
import com.example.attendanceapp.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels() {
        ViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarLogin.visibility = View.GONE

        binding.buttonLogin.setOnClickListener{

            var userId = binding.textInputEditTextUserId.text.toString()
            var password = binding.textInputEditTextPassword.text.toString()

            if (binding.textInputEditTextUserId.text.toString().isEmpty() ||
                binding.textInputEditTextPassword.text.toString().isEmpty()
            ) {
                showToast("All field must be filled")
            }
            else {
                viewModel.loginRequest(userId, password).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Log.d("LoginActivity", "Loading stories...")
                            binding.progressBarLogin.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBarLogin.visibility = View.GONE
                            val logindata = result.data
                            saveSession(logindata.username, logindata.userId)
                        }

                        is Result.Error -> {
                            binding.progressBarLogin.visibility = View.GONE
                            Log.e("LoginActivity", "Error: ${result.error}")
                            showToast(result.error)
                        }
                    }
                }
            }


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveSession(username: String, userId: String) {
        lifecycleScope.launch {
            viewModel.saveSession(
                UserModel(
                    username = username,
                    userId = userId,
                    isLogin = true
                )
            )

            delay(1000)
            withContext(Dispatchers.Main) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}