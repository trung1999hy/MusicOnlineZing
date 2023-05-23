package com.trungtv.Onmuzik.ui.audio

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trungtv.Onmuzik.R
import com.trungtv.Onmuzik.model.Song

class AudioAdapter(private val context: Context) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private var listData = ArrayList<Song>()
    private var onItemSelect: OnItemSelect? = null
    private val handler: Handler? = null
    private val runnable: Runnable? = null
    private val duration: Long = 0
    fun setData(listData: ArrayList<Song>) {
        this.listData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setDisplayView(listData[position])
        holder.itemView.setOnClickListener { v: View? -> onItemSelect?.onItemSelect(position) }
        holder.imgLike.setOnClickListener { v: View? -> onItemSelect?.onItemLike(position,listData[position]) }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView
        private val txtTime: TextView
        private val imgThumb: ImageView
        val imgLike: ImageView

        init {
            txtName = itemView.findViewById(R.id.txtName)
            txtTime = itemView.findViewById(R.id.txtTime)
            imgThumb = itemView.findViewById(R.id.imgThumb)
            imgLike = itemView.findViewById(R.id.like)
        }

        fun setDisplayView(songDto: Song) {
            txtName.text = songDto.name
            txtTime.text = songDto.performer
            Glide.with(itemView.context).load(songDto.thumbnail).placeholder(R.drawable.music)
                .into(imgThumb)
            Glide.with(itemView.context)
                .load(if (songDto.like) R.drawable.heart else R.drawable.love).into(imgLike)

        }
    }

    fun setOnItemSelect(onItemSelect: OnItemSelect?) {
        this.onItemSelect = onItemSelect
    }

    interface OnItemSelect {
        fun onItemSelect(pos: Int)
        fun onItemLike(pos: Int, songDto: Song)
    }
}