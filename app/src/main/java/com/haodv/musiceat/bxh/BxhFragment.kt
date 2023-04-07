package com.haodv.musiceat.bxh

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.haodv.musiceat.*
import com.haodv.musiceat.databinding.FragmentBxhBinding
import com.haodv.musiceat.firebase.Firebase
import com.haodv.musiceat.model.Data
import com.haodv.musiceat.model.Song

class BxhFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = BxhFragment()
    }

    private var audioAdapter: AudioAdapter? = null

    private var listValue = ArrayList<Song>()
    private var pos = 1

    private lateinit var viewModel: BxhViewModel

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
        viewModel = ViewModelProvider(this)[BxhViewModel::class.java]
        initView()

    }


    private fun initView() {
        audioAdapter = AudioAdapter(requireContext())
        audioAdapter?.setOnItemSelect(this)
        audio

    }

    private val audio: Unit
        private get() {
            Firebase().getSongList(object : Firebase.Callback<Data> {
                override fun Success(data: Data) {
                    listValue = data.song
                    val layoutManager = LinearLayoutManager(
                     requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    binding.listData.setHasFixedSize(true)
                    binding.listData.layoutManager = layoutManager
                    binding.listData.adapter = audioAdapter
                    audioAdapter!!.setData(data.song)
                }

                override fun Err(err: String?) {
                    Toast.makeText(
                        requireContext(),
                        err,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }


    override fun onItemSelect(pos: Int) {
        val songDto = listValue[pos]
        this.pos = pos
        songDto.play = true
        openPlayController(songDto)

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