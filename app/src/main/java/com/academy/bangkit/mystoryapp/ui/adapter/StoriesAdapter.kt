package com.academy.bangkit.mystoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.network.response.Story
import com.academy.bangkit.mystoryapp.databinding.ItemStoryBinding
import com.academy.bangkit.mystoryapp.ui.story.detail.DetailStoryActivity
import com.academy.bangkit.mystoryapp.utils.StoryDiffCallback
import com.bumptech.glide.Glide

class StoriesAdapter :
    RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    private val listStory = ArrayList<Story>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StoryViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = listStory.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = listStory[position]
        holder.bind(data)
    }

    inner class StoryViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Story) {
            binding.apply {
                thumbnailIv.loadImage(data.photoUrl)
                nameTv.text = data.name
                descTv.text = data.description
            }

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.thumbnailIv, "thumbnail"),
                        Pair(binding.nameTv, "name"),
                        Pair(binding.descTv, "desc")
                    )

                val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.PHOTO_URL_EXTRA, data.photoUrl)
                    putExtra(DetailStoryActivity.NAME_EXTRA, data.name)
                    putExtra(DetailStoryActivity.DESC_EXTRA, data.description)
                }

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    private fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(this)
    }

    fun setListStory(newListStory: List<Story>) {
        val diffCallback = StoryDiffCallback(listStory, newListStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listStory.clear()
        listStory.addAll(newListStory)
        diffResult.dispatchUpdatesTo(this)
    }

}