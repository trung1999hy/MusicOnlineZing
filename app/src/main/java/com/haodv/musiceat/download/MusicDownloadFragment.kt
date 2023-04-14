package com.haodv.musiceat.download

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.haodv.musiceat.*
import com.haodv.musiceat.databinding.FragmentMusicDownloadBinding
import com.haodv.musiceat.model.Song

class MusicDownloadFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = MusicDownloadFragment()
    }

    private var audioAdapter: AudioAdapter? = null
    private var pos = 0

    private  val viewModel: MainViewModel by activityViewModels()
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
        (activity as MainActivity)?.setVisibility(View.VISIBLE)
        viewModel.getMp3Songs()
        audioAdapter = AudioAdapter(requireContext())
        audioAdapter?.setOnItemSelect(this)
        binding.rvData.adapter = audioAdapter
        viewModel.lisMusicLocal.observe(viewLifecycleOwner) {songList ->
            audioAdapter?.setData(songList.filter { it.like } as ArrayList<Song>)
        }


    }

    override fun onItemSelect(pos: Int) {
        val songDto = viewModel.lisMusicLocal.value?.get(pos)
        this.pos = pos
        songDto?.play = true
        songDto?.let { openPlayController(it) }

    }

    override fun onItemLike(pos: Int, songDto: Song) {
        songDto.apply {
            this.like = !like
        }
        viewModel.upDateSong(pos, songDto)
        audioAdapter?.notifyItemChanged(pos)
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
                viewModel.lisMusicLocal.value?.filter { it.like } as ArrayList<Song>,
                pos,
            )
        )
            .addToBackStack(AudioFragment::class.java.name).commit()
    }

}