package com.haodv.musiceat.download

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haodv.musiceat.AudioAdapter
import com.haodv.musiceat.AudioFragment
import com.haodv.musiceat.MainActivity
import com.haodv.musiceat.R
import com.haodv.musiceat.databinding.FragmentMusicDownloadBinding
import com.haodv.musiceat.model.Song

class MusicDownloadFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = MusicDownloadFragment()
    }

    private var audioAdapter: AudioAdapter? = null
    private var pos = 0

    private lateinit var viewModel: MusicDownloadViewModel
    private lateinit var binding: FragmentMusicDownloadBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicDownloadBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MusicDownloadViewModel::class.java]
        (activity as MainActivity)?.setVisibility(View.VISIBLE)
        viewModel.getMp3Songs(requireContext())
        audioAdapter = AudioAdapter(requireContext())
        audioAdapter?.setOnItemSelect(this)
        binding.rvData.adapter = audioAdapter
        viewModel.lisMusicLocal.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                audioAdapter?.setData(it.reversed() as ArrayList<Song>)
        }


    }

    override fun onItemSelect(pos: Int) {
        val songDto = viewModel.lisMusicLocal.value?.get(pos)
        this.pos = pos
        songDto?.play = true
        songDto?.let { openPlayController(it) }

    }

    private fun openPlayController(songDto: Song) {
        val manager = requireActivity().supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_top,
            R.anim.slide_out_top
        )
        transaction.add(
            R.id.frameLayout,
            AudioFragment.newInstance(
                viewModel.lisMusicLocal.value?.reversed() as ArrayList<Song>,
                pos,
            )
        )
            .addToBackStack(AudioFragment::class.java.name).commit()
    }

}