package com.trungtv.Onmuzik

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.trungtv.Onmuzik.charts.ChartsFragment
import com.trungtv.Onmuzik.like.LikeFragment

class MainViewPager(fm : FragmentManager) : FragmentPagerAdapter (fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> ChartsFragment.newInstance()
            1 -> LikeFragment.newInstance()
            else -> ChartsFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Bảng xếp hạng"
            1 -> "Bài hát ưa thích"
            else -> super.getPageTitle(position)
        }

    }
}