package com.trungtv.Onmuzik.ui.lyric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trungtv.Onmuzik.databinding.FragmentLyricBinding
import com.trungtv.Onmuzik.model.LyricLine

class LyricFragment : Fragment() {
    private  var lyricsAdapter: LyricsAdapter =  LyricsAdapter()
    private lateinit var binding: FragmentLyricBinding
    private var listLyricLine : ArrayList<LyricLine> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLyricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lyricsRecyclerView.adapter = lyricsAdapter
        lyricsAdapter.setData(listLyricLine)
    }
    fun setDataLyric(lyrics : ArrayList<LyricLine>){

        lyricsAdapter.setData(lyrics)
    }

    companion object {

        fun newInstance() = LyricFragment()
    }
}