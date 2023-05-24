package com.academy.bangkit.mystoryapp.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.databinding.ActivityWelcomeBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity
import com.academy.bangkit.mystoryapp.ui.auth.signup.SignupActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class WelcomeActivity : AppCompatActivity() {

    private val welcomeViewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory(UserPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        welcomeViewModel.setIsLogin(false)

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
            startActivity(Intent(this@WelcomeActivity, SignupActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        }
    }
}