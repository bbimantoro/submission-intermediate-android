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
import com.academy.bangkit.mystoryapp.data.network.response.Story
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

        storiesAdapter = StoriesAdapter()

        mainBinding.storyRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = storiesAdapter
        }

        mainViewModel.result.observe(this) { result ->
            observerMainStory(result)
        }
    }

    override fun onResume() {
        super.onResume()
        checkUserSession()
    }

    private fun checkUserSession() {
        mainViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                setupDataView("Bearer $token")
            }
        }
    }

    private fun setupDataView(token: String) {
        mainViewModel.getAllStories(token)
    }

    private fun observerMainStory(result: Result<List<Story>>) {
        when (result) {
            is Result.Success -> {
                mainBinding.progressbar.visibility = View.GONE

                val storyData = result.data

                if (storyData.isEmpty()) {
                    mainBinding.emptyStoryTv.visibility = View.VISIBLE
                } else {
                    mainBinding.emptyStoryTv.visibility = View.GONE
                    storiesAdapter.setListStory(storyData)
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