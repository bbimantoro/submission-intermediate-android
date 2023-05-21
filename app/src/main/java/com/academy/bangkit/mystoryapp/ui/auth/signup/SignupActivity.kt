package com.academy.bangkit.mystoryapp.ui.auth.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.signupBtn.setOnClickListener {

            val name = binding.nameEdt.text.toString()
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameEdt.error = getString(R.string.err_name)
                }

                email.isEmpty() -> {
                    binding.nameEdt.error = getString(R.string.err_email)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password)
                }

                else -> {
                    signupViewModel.signup(
                        name,
                        email,
                        password
                    )
                }
            }

            signupViewModel.result.observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(
                            this,
                            getString(R.string.signup_success_message),
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, LoginActivity::class.java)
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