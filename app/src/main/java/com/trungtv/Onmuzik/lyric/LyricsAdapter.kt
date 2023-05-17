package com.trungtv.Onmuzik.lyric

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trungtv.Onmuzik.databinding.ItemLyricBinding
import com.trungtv.Onmuzik.model.LyricLine

class LyricsAdapter : RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder>() {
    private var listLyric : ArrayList<LyricLine> = arrayListOf()
    inner class LyricsViewHolder(val binding: ItemLyricBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(lyricLine: LyricLine){
                binding.lyricsTextView.text= lyricLine.text
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricsViewHolder {
        val binding = ItemLyricBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  LyricsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listLyric.size
    }

    override fun onBindViewHolder(holder: LyricsViewHolder, position: Int) {
       holder.bind(listLyric[position])
    }
    fun setData(list: ArrayList<LyricLine>){
        this.listLyric = list
        notifyDataSetChanged()
    }
}