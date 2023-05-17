package com.trungtv.Onmuzik

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.trungtv.Onmuzik.bxh.BxhFragment
import com.trungtv.Onmuzik.download.MusicDownloadFragment

class MainViewPager(fm : FragmentManager) : FragmentPagerAdapter (fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> BxhFragment.newInstance()
            1 -> MusicDownloadFragment.newInstance()
            else -> BxhFragment.newInstance()
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