package com.trungtv.Onmuzik.ui.like

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.trungtv.Onmuzik.*
import com.trungtv.Onmuzik.ui.audio.AudioAdapter
import com.trungtv.Onmuzik.ui.audio.AudioFragment
import com.trungtv.Onmuzik.databinding.FragmentMusicDownloadBinding
import com.trungtv.Onmuzik.model.Song
import com.trungtv.Onmuzik.ui.MainActivity
import com.trungtv.Onmuzik.ui.MainApp
import com.trungtv.Onmuzik.ui.MainViewModel
import com.trungtv.Onmuzik.ui.PurchaseInAppActivity

class LikeFragment : Fragment(), AudioAdapter.OnItemSelect {

    companion object {
        fun newInstance() = LikeFragment()
    }

    private var audioAdapter: AudioAdapter? = null
    private var pos = 0

    private val viewModel: MainViewModel by activityViewModels()
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
        viewModel.lisMusicLocal.observe(viewLifecycleOwner) { songList ->
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
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage("Bạn có muốn thêm vào danh sách yêu thích không và trừ 1 vàng ?")
        dialog.setTitle("Thêm ? ")

        dialog.setPositiveButton("Oke") { dialog, which ->
            if (!songDto.like) {
                MainApp.newInstance()?.preference?.apply {
                    if (getValueCoin() > 1) {
                        setValueCoin(getValueCoin() - 1)
                        songDto.apply {
                            this.like = !like
                        }
                        viewModel.upDateSong(pos, songDto)
                        audioAdapter?.notifyItemChanged(pos)
                        (activity as? MainActivity)?.txtCoin?.text = String.format(resources.getString(R.string.value_coin), getValueCoin())
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