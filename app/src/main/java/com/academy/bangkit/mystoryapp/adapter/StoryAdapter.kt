package com.academy.bangkit.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.databinding.ItemStoryBinding
import com.academy.bangkit.mystoryapp.ui.story.detail.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter :
    PagingDataAdapter<StoryEntity, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)

        holder.bind(data)
    }

    inner class MyViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryEntity?) {
            binding.apply {
                thumbnailIv.loadImage(data?.photoUrl)
                nameTv.text = data?.name
                descTv.text = data?.description

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
                    putExtra(DetailStoryActivity.PHOTO_URL_EXTRA, data?.photoUrl)
                    putExtra(DetailStoryActivity.NAME_EXTRA, data?.name)
                    putExtra(DetailStoryActivity.DESC_EXTRA, data?.description)
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

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean =
                oldItem == newItem


            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean =
                oldItem.id == newItem.id
        }
    }
}