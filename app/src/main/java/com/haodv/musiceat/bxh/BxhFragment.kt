package com.haodv.musiceat.bxh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.haodv.musiceat.AudioAdapter
import com.haodv.musiceat.AudioFragment
import com.haodv.musiceat.MainActivity
import com.haodv.musiceat.R
import com.haodv.musiceat.databinding.FragmentBxhBinding
import com.haodv.musiceat.MainViewModel
import com.haodv.musiceat.model.Song

class BxhFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = BxhFragment()
    }

    private var audioAdapter: AudioAdapter? = null

    private var listValue = ArrayList<Song>()
    private var pos = 1
    private val viewModel: MainViewModel by activityViewModels()


    private lateinit var binding: FragmentBxhBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBxhBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

    }


    private fun initView() {
        (activity as MainActivity)?.setVisibility(View.VISIBLE)
        audioAdapter = AudioAdapter(requireContext())
        audioAdapter?.setOnItemSelect(this)
        viewModel.getMp3Songs()

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.listData.setHasFixedSize(true)
        binding.listData.layoutManager = layoutManager
        binding.listData.adapter = audioAdapter

        viewModel.lisMusicLocal.observe(viewLifecycleOwner) {
            audioAdapter!!.setData(it)
            listValue = it
        }

    }


    override fun onItemSelect(pos: Int) {
        val songDto = listValue[pos]
        this.pos = pos
        songDto.play = true
        openPlayController(songDto)

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
        transaction.add(R.id.frameLayout, AudioFragment.newInstance(listValue, pos))
            .addToBackStack(AudioFragment::class.java.name).commit()

    }

}