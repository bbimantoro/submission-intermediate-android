package com.academy.bangkit.mystoryapp.ui.story.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.databinding.FragmentHomeBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.adapter.LoadingStateAdapter
import com.academy.bangkit.mystoryapp.ui.adapter.StoryAdapter
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapter: StoryAdapter

    private var _homeBinding: FragmentHomeBinding? = null
    private val homeBinding get() = _homeBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.storyResult.observe(viewLifecycleOwner, observerMainStory)
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()

        homeBinding.apply {
            swipeRefresh.setOnRefreshListener {
                refresh()
            }

            storyRv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@HomeFragment.adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        retry()
                    }
                )
            }
        }

        adapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && adapter.itemCount < 1) {
                Snackbar.make(
                    requireActivity().window.decorView,
                    getString(R.string.story_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val observerMainStory = Observer<PagingData<StoryEntity>> {
        adapter.submitData(lifecycle, it)
        homeBinding.apply {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun retry() = adapter.retry()
    private fun refresh() = adapter.refresh()

    override fun onDestroyView() {
        super.onDestroyView()
        _homeBinding = null
    }
}