package com.squirrel.lottonumberone.ui.number_select

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squirrel.lottonumberone.R
import com.squirrel.lottonumberone.base.BaseBottomSheetDialogFragment
import com.squirrel.lottonumberone.databinding.FragmentNumberCheckBinding
import com.squirrel.lottonumberone.ui.number_select.adapter.NumberAdapter


class NumberCheckFragment : BaseBottomSheetDialogFragment<FragmentNumberCheckBinding>(
    FragmentNumberCheckBinding::inflate
) {

    private val viewModel: NumberViewModel by viewModels()
    var onNumberChecked: ((MutableList<Int>) -> Unit)? = null

    override fun getTheme(): Int = R.style.Theme_LottoNumberOne_BottomSheet

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
            bottomSheet?.let { sheet ->
                BottomSheetBehavior.from(sheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                }
            }
        }
        return dialog
    }

    override fun setUpInit() {
        mBinding.numberListView.adapter = NumberAdapter(requireContext(), viewModel)
        setUpListener()
    }

    private fun setUpListener() {
        mBinding.saveButton.setOnClickListener {
            if (viewModel.numberArray.value!!.size < 6) {
                Toast.makeText(context, "번호 6개를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onNumberChecked?.invoke(viewModel.numberArray.value!!)
            dismiss()
        }
    }

    override fun observeViewModel() {}

}
