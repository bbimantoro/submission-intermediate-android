package com.academy.bangkit.mystoryapp.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.academy.bangkit.mystoryapp.ui.story.main.HomeFragment
import com.academy.bangkit.mystoryapp.ui.story.maps.MapsFragment

private const val TAB_COUNT = 2

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = HomeFragment()
            1 -> fragment = MapsFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int = TAB_COUNT
}