package com.academy.bangkit.mystoryapp.ui.auth.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.databinding.ActivityLoginBinding
import com.academy.bangkit.mystoryapp.ui.UserViewModelFactory
import com.academy.bangkit.mystoryapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        UserViewModelFactory(UserPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.loginBtn.setOnClickListener {
            val name = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            when {
                name.isEmpty() -> {
                    binding.emailEdt.error = getString(R.string.err_email_field)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password_field)
                }

                else -> {
                    loginViewModel.login(name, password)
                }
            }

            loginViewModel.result.observe(this) { result -> observerLogin(result) }
        }
    }

    private fun observerLogin(result: Result<LoginResponse>) {
        when (result) {
            is Result.Loading -> {
                binding.progressbar.visibility = View.VISIBLE
            }

            is Result.Success -> {
                binding.progressbar.visibility = View.GONE

                val data = result.data.loginResult?.token
                data?.let {
                    loginViewModel.saveToken(it)
                }
                Log.d("LoginActivity", "token: $data")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            is Result.Error -> {
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
