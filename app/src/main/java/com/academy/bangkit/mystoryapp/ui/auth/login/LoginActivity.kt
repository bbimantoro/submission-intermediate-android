package com.academy.bangkit.mystoryapp.ui.auth.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.databinding.ActivityLoginBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            when {
                email.isEmpty() -> {
                    binding.emailEdt.error = getString(R.string.err_email_field)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password_field)
                }

                else -> {
                    viewModel.login(email, password)
                }
            }

            viewModel.loginResult.observe(this, observerLogin)
        }
    }

    private val observerLogin = Observer<Result<LoginResponse>> { result ->
        when (result) {
            is Result.Loading -> {
                binding.progressbar.visibility = View.VISIBLE
            }

            is Result.Error -> {
                binding.progressbar.visibility = View.GONE
                showToast(result.error)
            }

            is Result.Success -> {
                binding.progressbar.visibility = View.GONE

                val token = result.data.loginResult?.token
                token?.let { viewModel.saveCredential(token) }
                // Log.d("MainActivity", "token: $token")

                moveToMain()
            }
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}
