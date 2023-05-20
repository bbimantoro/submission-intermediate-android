package com.academy.bangkit.mystoryapp.ui.auth.signup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.databinding.ActivitySignupBinding
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.signupBtn.setOnClickListener {
            val name = binding.nameEdt.text.toString().trim()
            val email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameEdt.error = getString(R.string.err_name)
                }

                email.isEmpty() -> {
                    binding.emailEdt.error = getString(R.string.err_email)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password)
                }

                else -> {
                    signupViewModel.signup(name, email, password)

                    signupViewModel.result.observe(this) { result ->
                        when (result) {
                            is Result.Success -> {
                                Toast.makeText(
                                    this,
                                    getString(R.string.signup_success_message),
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                                startActivity(intent)
                            }

                            is Result.Error -> {
                                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            }

                            is Result.Loading -> {}
                        }
                    }
                }
            }
        }
    }
}