package com.academy.bangkit.mystoryapp.ui.auth.signup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.databinding.ActivitySignupBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        signupViewModel.signupResult.observe(this, observeSignup)
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
        binding.signupBtn.setOnClickListener {

            val name = binding.nameEdt.text.toString()
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameEdt.error = getString(R.string.err_name_field)
                }

                email.isEmpty() -> {
                    binding.nameEdt.error = getString(R.string.err_email_field)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password_field)
                }

                else -> {
                    signupViewModel.signup(
                        name,
                        email,
                        password
                    )
                }
            }
        }
    }

    private val observeSignup = Observer<Result<CommonResponse>> { result ->
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

                showToast(getString(R.string.signup_success_message))
                moveToLogin()
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}