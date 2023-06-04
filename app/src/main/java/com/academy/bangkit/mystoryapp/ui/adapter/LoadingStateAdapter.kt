package com.academy.bangkit.mystoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.academy.bangkit.mystoryapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder = LoadingStateViewHolder(
        ItemLoadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ), retry
    )

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryBtn.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                if (loadState is LoadState.Error) {
                    errorMsgTv.text = loadState.error.localizedMessage
                }

                progressBar.isVisible = loadState is LoadState.Loading
                retryBtn.isVisible = loadState is LoadState.Error
                errorMsgTv.isVisible = loadState is LoadState.Error
            }
        }

    }
}