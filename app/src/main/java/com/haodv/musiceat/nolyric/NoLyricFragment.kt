package com.haodv.musiceat.nolyric

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haodv.musiceat.R

class NoLyricFragment : Fragment() {

    companion object {
        fun newInstance() = NoLyricFragment()
    }

    private lateinit var viewModel: NoLyricViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_no_lyric, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NoLyricViewModel::class.java)
        // TODO: Use the ViewModel
    }

}