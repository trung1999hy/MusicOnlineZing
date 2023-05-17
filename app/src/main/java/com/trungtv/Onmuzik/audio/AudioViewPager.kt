package com.trungtv.Onmuzik.audio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AudioViewPager(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var list: ArrayList<Fragment> = arrayListOf()
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return list.get(position)
    }
    fun setData(list: ArrayList<Fragment>){
        this.list = list
        notifyDataSetChanged()
    }

}