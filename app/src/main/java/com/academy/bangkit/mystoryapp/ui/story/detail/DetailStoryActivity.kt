package com.academy.bangkit.mystoryapp.ui.story.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.academy.bangkit.mystoryapp.databinding.ActivityDetailStoryBinding
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupData()
    }

    private fun setupData() {
        val photoUrl = intent.getStringExtra(PHOTO_URL_EXTRA)
        val name = intent.getStringExtra(NAME_EXTRA)
        val description = intent.getStringExtra(DESC_EXTRA)

        binding.apply {
            contentDetail.nameTv.text = name
            contentDetail.descTv.text = description
        }

        Glide.with(this).load(photoUrl).into(binding.contentDetail.thumbnailIv)
    }

    companion object {
        const val PHOTO_URL_EXTRA = "photo_extra"
        const val NAME_EXTRA = "name_extra"
        const val DESC_EXTRA = "desc_extra"
    }
}