package com.academy.bangkit.mystoryapp.ui.story.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.databinding.ActivityMainBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.adapter.StoriesAdapter
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity
import com.academy.bangkit.mystoryapp.ui.story.post.PostStoryActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreferences.getInstance(dataStore))
    }

    private lateinit var storiesAdapter: StoriesAdapter

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.addStoryFab.setOnClickListener {
            val intent = Intent(this, PostStoryActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        setupViewModel(mainViewModel)
    }

    private fun setupRecyclerView() {
        storiesAdapter = StoriesAdapter()

        mainBinding.storyRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = storiesAdapter
        }
    }

    private fun setupViewModel(mainViewModel: MainViewModel) {
        mainViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setupViewData(mainViewModel, "Bearer $token")
            }
        }
    }

    private fun setupViewData(mainViewModel: MainViewModel, token: String) {

        mainViewModel.getAllStories(token)

        mainViewModel.result.observe(this) { result ->

            when (result) {
                is Result.Success -> {

                    mainBinding.progressbar.visibility = View.GONE

                    val storyData = result.data

                    if (storyData.isEmpty()) {
                        mainBinding.apply {
                            emptyStoryTv.visibility = View.VISIBLE
                            storyRv.visibility = View.GONE
                        }
                    } else {
                        mainBinding.apply {
                            emptyStoryTv.visibility = View.GONE
                            storyRv.visibility = View.VISIBLE
                            storiesAdapter.setListStory(storyData)
                        }
                    }
                }

                is Result.Loading -> {
                    mainBinding.progressbar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    mainBinding.progressbar.visibility = View.GONE

                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.logout -> {
                mainViewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}