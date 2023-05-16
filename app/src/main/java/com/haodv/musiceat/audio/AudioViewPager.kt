package com.haodv.musiceat.audio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haodv.musiceat.bxh.BxhFragment
import com.haodv.musiceat.lyric.LyricFragment
import com.haodv.musiceat.nolyric.NoLyricFragment

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