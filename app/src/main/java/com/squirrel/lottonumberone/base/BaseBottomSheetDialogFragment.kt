package com.squirrel.lottonumberone.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.squirrel.lottonumberone.utils.SingleEvent
import com.squirrel.lottonumberone.utils.showToast
import java.util.zip.Inflater

abstract class BaseBottomSheetDialogFragment<B : ViewBinding>(
    private val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> B
) : BottomSheetDialogFragment() {

    private var _binding: B? = null
    val mBinding get() = _binding!!

    val mProgressTime = 1000L

    abstract fun observeViewModel()
    abstract fun setUpInit()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingFactory.invoke(inflater, container, false)
        //DataBindingUtil.findBinding(inflate.invoke(inflater, container, false).root)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpInit()
        observeViewModel()
    }

    fun observeToast(event: LiveData<SingleEvent<Any>>) {
        mBinding.root.showToast(this, event, Snackbar.LENGTH_SHORT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->

            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            parentLayout?.let {
                val behavior = BottomSheetBehavior.from(it)
                setupFullHeight(it)  // 높이 설정
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // 기본 상태를 펼쳐진 상태로 설정
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getScreenHeight() // 전체 화면 높이로 설정
        bottomSheet.layoutParams = layoutParams
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels // 디바이스 전체 화면 높이 가져오기
    }



}