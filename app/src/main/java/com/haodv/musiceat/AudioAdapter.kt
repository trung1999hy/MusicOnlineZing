package com.haodv.musiceat

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haodv.musiceat.model.Song

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
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView
        private val txtTime: TextView
        private val imgThumb: ImageView

        init {
            txtName = itemView.findViewById(R.id.txtName)
            txtTime = itemView.findViewById(R.id.txtTime)
            imgThumb = itemView.findViewById(R.id.imgThumb)
        }

        fun setDisplayView(songDto: Song) {
            txtName.text = songDto.name
            txtTime.text = songDto.performer
            Glide.with(itemView.context).load(songDto.thumbnail).placeholder(R.drawable.music).into(imgThumb)
        }
    }

    fun setOnItemSelect(onItemSelect: OnItemSelect?) {
        this.onItemSelect = onItemSelect
    }

    interface OnItemSelect {
        fun onItemSelect(pos: Int)
    }
}