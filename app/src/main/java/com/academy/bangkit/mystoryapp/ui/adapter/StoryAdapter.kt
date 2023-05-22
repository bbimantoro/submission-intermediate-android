package com.academy.bangkit.mystoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.academy.bangkit.mystoryapp.data.network.response.Story
import com.academy.bangkit.mystoryapp.ui.adapter.StoryAdapter.MyViewHolder
import com.academy.bangkit.mystoryapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide

class StoryAdapter(private val listStory: (Story) -> Unit) :
    ListAdapter<Story, MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
    }

    inner class MyViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            with(binding) {
                // thumbnailIv.loadImage()
            }
        }
    }

    private fun ImageView.loadImage(url: String?) {
        Glide.with(context)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem == newItem
                }
            }
    }
}