package com.example.attendanceapp.ui.change_password

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.ActivityChangePasswordBinding
import com.example.attendanceapp.databinding.ActivityLoginBinding
import com.example.attendanceapp.repository.Result
import com.example.attendanceapp.ui.login.LoginActivity
import com.example.attendanceapp.ui.login.LoginViewModel
import com.example.attendanceapp.ui.main.MainActivity

class ChangePasswordActivity : AppCompatActivity() {
    private var _binding: ActivityChangePasswordBinding? = null
    private val binding get() = _binding!!
    private var userId: String = ""

    private val viewModel: ChangePasswordViewModel by viewModels() {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarChangePassword.visibility = View.GONE

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                userId = user.userId
            }

        }

        binding.btnBackChangePassword.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateTo", "Account")
            startActivity(intent)
            finish()
        }

        binding.buttonChangePassword.setOnClickListener {
            val currentPassword = binding.textInputEditTextCurrentPassword.text.toString()
            val newPassword = binding.textInputEditTextNewPassword.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                showToast("All field must be filled")
            }
            else {
                viewModel.updatePasswordRequest(userId, currentPassword, newPassword).observe(this) { result ->
                    when (result) {
                        is Result.Loading-> {
                            binding.progressBarChangePassword.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBarChangePassword.visibility = View.GONE
                            val updatePasswordResultData = result.data
                            showToast(updatePasswordResultData.message)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("navigateTo", "Account")
                            startActivity(intent)
                            finish()
                        }
                        is Result.Error -> {
                            binding.progressBarChangePassword.visibility = View.GONE
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
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}