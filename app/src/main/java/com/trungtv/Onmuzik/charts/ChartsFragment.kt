package com.trungtv.Onmuzik.charts

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.trungtv.Onmuzik.*
import com.trungtv.Onmuzik.audio.AudioAdapter
import com.trungtv.Onmuzik.audio.AudioFragment
import com.trungtv.Onmuzik.databinding.FragmentChartsBinding
import com.trungtv.Onmuzik.model.Song

class ChartsFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = ChartsFragment()
    }

    private var audioAdapter: AudioAdapter? = null

    private var listValue = ArrayList<Song>()
    private var pos = 1
    private val viewModel: MainViewModel by activityViewModels()


    private lateinit var binding: FragmentChartsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartsBinding.inflate(layoutInflater, container, false)
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
        val dialog = AlertDialog.Builder(requireContext())
        if (songDto.like) {
            dialog.setMessage("Bạn có muốn xóa khỏi danh sách yêu thích không?")
                .setTitle("Xóa ?")
        } else {
            dialog.setMessage("Bạn có muốn thêm vào danh sách yêu thích không và trừ 1 vàng ?")
            dialog.setTitle("Thêm ? ")
        }
        dialog.setPositiveButton("Oke") { dialog, which ->
            if (!songDto.like) {
                MainApp.newInstance()?.preference?.apply {
                    if (getValueCoin() > 1) {
                        setValueCoin(getValueCoin() - 1)
                        songDto.apply {
                            this.like = !like
                        }
                        (activity as? MainActivity)?.txtCoin?.text = String.format(resources.getString(R.string.value_coin), getValueCoin())
                        viewModel.upDateSong(pos, songDto)
                        audioAdapter?.notifyItemChanged(pos)
                        Toast.makeText(
                            requireContext(),
                            "Đã thêm  thành công và trù 1 vàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else startActivity(
                        Intent(
                            requireContext(),
                            PurchaseInAppActivity::class.java
                        )
                    )
                }
            } else {
                songDto.apply {
                    this.like = !like
                }
                viewModel.upDateSong(pos, songDto)
                audioAdapter?.notifyItemChanged(pos)
                Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.setNegativeButton("Dismiss") { dialog, which ->
            dialog.dismiss()
        }
            dialog.create()
        dialog.show()
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