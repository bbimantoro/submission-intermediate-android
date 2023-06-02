package com.academy.bangkit.mystoryapp.ui.story.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.databinding.ActivityMainBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.adapter.SectionsPagerAdapter
import com.academy.bangkit.mystoryapp.ui.story.post.PostStoryActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.logout -> {
                    mainViewModel.logout()
                    true
                }

                else -> false
            }
        }

        mainBinding.addStoryFab.setOnClickListener {
            val intent = Intent(this, PostStoryActivity::class.java)
            startActivity(intent)
        }

        setupPagerAdapter()
    }

    private fun setupPagerAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        mainBinding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(mainBinding.tabs, mainBinding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}