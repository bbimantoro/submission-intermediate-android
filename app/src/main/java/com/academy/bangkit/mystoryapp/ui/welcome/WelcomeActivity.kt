package com.academy.bangkit.mystoryapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.academy.bangkit.mystoryapp.databinding.ActivityWelcomeBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity
import com.academy.bangkit.mystoryapp.ui.auth.signup.SignupActivity
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity

class WelcomeActivity : AppCompatActivity() {

    private val viewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        viewModel.checkCredential().observe(this) { isLogin ->
            if (isLogin) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
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
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun playAnimation() {
        val welcomeIv = ObjectAnimator.ofFloat(binding.WelcomeIv, View.ALPHA, 1f)
        val title = ObjectAnimator.ofFloat(binding.TitleTv, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.descTv, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.loginBtn, View.ALPHA, 1f).setDuration(500)
        val signupBtn = ObjectAnimator.ofFloat(binding.signupBtn, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginBtn, signupBtn)
        }

        AnimatorSet().apply {
            playSequentially(welcomeIv, title, desc, together)
            start()
        }
    }
}