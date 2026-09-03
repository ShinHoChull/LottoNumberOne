package com.squirrel.lottonumberone.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.squirrel.lottonumberone.config.Defines
import com.squirrel.lottonumberone.databinding.DialogMixNumbersBinding
import com.squirrel.lottonumberone.ui.dialog.adapter.MixNumberAdapter
import com.squirrel.lottonumberone.ui.home.HomeViewModel

class MixNumbersDialog: DialogFragment() {

    private lateinit var mBinding: DialogMixNumbersBinding
    private val viewModel: HomeViewModel by activityViewModels()

    private var numberList: MutableList<MutableList<Int>>? = null
    private lateinit var adapter: MixNumberAdapter


    init {
        setCancelable(true)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogMixNumbersBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mixType = "exclude"

        if (arguments != null) {
            arguments?.getString("mixType")
        }

        if (mixType == "include") adapter = MixNumberAdapter(viewModel.includingMixNumberArray.value!!)
        else if (mixType == "exclude") adapter = MixNumberAdapter(viewModel.includingMixNumberArray.value!!)

        mBinding.lottoList.adapter = adapter

        setUpListener()
    }

    private fun setUpListener() {
        mBinding.btnClose.setOnClickListener { dismiss() }
    }


}