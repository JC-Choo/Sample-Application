package dev.chu.memo.view.activity.memo.read

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chu.memo.R
import dev.chu.memo.base.BaseFragment
import dev.chu.memo.common.Const
import dev.chu.memo.data.local.MemoData
import dev.chu.memo.databinding.FragmentReadBinding
import dev.chu.memo.etc.extension.*
import dev.chu.memo.etc.listener.OnBackPressedListener
import dev.chu.memo.view.adapter.ImageAdapter
import dev.chu.memo.view_model.RoomViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReadFragment : BaseFragment<FragmentReadBinding>(), OnBackPressedListener {
    @LayoutRes
    override fun layoutRes(): Int = R.layout.fragment_read

    companion object {
        fun newInstance(memoId: Int) = ReadFragment().apply {
            arguments = Bundle().apply {
                putInt(Const.ARGS.MEMO_ID, memoId)
            }
        }
    }

    private val roomVM: RoomViewModel by viewModel()
    private val adapter: ImageAdapter by inject()

    private var memoId: Int = 0
    private var data: MemoData? = null

    interface FCallback {
        fun onClickEtc()
    }

    private var callback: FCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach")

        activity?.let {
            if(it is FCallback) {
                callback = activity as FCallback
            }
        }
    }

    override fun setView(view: View?, savedInstanceState: Bundle?, arguments: Bundle?) {
        Log.i(TAG, "setView")

        binding.viewModel = roomVM

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setActionBarHome(binding.includeToolbar.toolbar, R.drawable.arrow_back_white)
        binding.includeToolbar.toolbarTv.text = ""
        binding.includeToolbar.toolbarTvDelete.visibility = View.VISIBLE
        binding.includeToolbar.toolbarTvDelete.setOnClickListener {
            data?.let {
                context?.confirmDialog(R.string.do_you_delete_this_memo, DialogInterface.OnClickListener { dialog, which ->
                    roomVM.deleteMemo(it, false)
                    activity?.finish()
                })
            } ?: run {
                showToast("결과 잘못 불러옴")
            }
        }
        binding.includeToolbar.toolbarTvEtc.text = getString(R.string.modify)
        binding.includeToolbar.toolbarTvEtc.setOnClickListener {
            callback?.onClickEtc()
            binding.includeToolbar.toolbarTvDelete.visibility = View.GONE
            binding.includeToolbar.toolbarTvEtc.isEnabled = false
        }

        arguments?.let {
            memoId = it.getInt(Const.ARGS.MEMO_ID, 0)
        }

        roomVM.getDataById(memoId)

        setRecyclerView()
        observeViewModel()
    }

    private fun setRecyclerView() {
        binding.readFlRvImage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.readFlRvImage.adapter = adapter
    }

    private fun observeViewModel() {
        roomVM.memo.observe(this, Observer {
            data = it
            roomVM.title.value = it.title
            roomVM.content.value = it.content

            if(!it.imageUrls.isNullOrEmpty())
                adapter.setItems(it.imageUrls!!)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            activity?.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        activity?.finish()
    }
}