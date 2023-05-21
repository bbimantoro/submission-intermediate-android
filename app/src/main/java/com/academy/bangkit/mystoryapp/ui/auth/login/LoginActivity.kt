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
import com.academy.bangkit.mystoryapp.MainActivity
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.databinding.ActivityLoginBinding
import com.academy.bangkit.mystoryapp.ui.UserViewModelFactory

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
                    binding.emailEdt.error = getString(R.string.err_email)
                }

                password.isEmpty() -> {
                    binding.passwordEdt.error = getString(R.string.err_password)
                }

                else -> {
                    loginViewModel.login(name, password)
                }
            }

            loginViewModel.result.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        binding.progressbar.visibility = View.GONE
                        val error = result.error
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Success -> {
                        binding.progressbar.visibility = View.GONE

                        val data = result.data.loginResult?.token
                        data?.let {
                            loginViewModel.saveToken(it)
                        }
                        Log.d("LoginActivity", "Token : $data")

                        Toast.makeText(
                            this,
                            getString(R.string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
