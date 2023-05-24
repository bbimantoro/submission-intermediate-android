package com.academy.bangkit.mystoryapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.academy.bangkit.mystoryapp.data.network.response.Story

class StoryDiffCallback(
    private val oldStoryList: List<Story>,
    private val newStoryList: List<Story>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldStoryList.size

    override fun getNewListSize() = newStoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldStoryList[oldItemPosition].id == newStoryList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldStoryList[oldItemPosition] == newStoryList[newItemPosition]
}