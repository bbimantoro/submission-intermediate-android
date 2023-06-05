package com.academy.bangkit.mystoryapp.ui.story.main

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.academy.bangkit.mystoryapp.databinding.FragmentHomeBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.adapter.LoadingStateAdapter
import com.academy.bangkit.mystoryapp.adapter.StoryAdapter


class HomeFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapter: StoryAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

    }

    private fun getData() {
        adapter = StoryAdapter()

        binding.storyRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
        }

        binding.storyRv.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.storyResult.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}