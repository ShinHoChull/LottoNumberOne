package com.squirrel.lottonumberone.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.squirrel.lottonumberone.databinding.DialogScanResultBinding

class ScanResultDialog : DialogFragment() {

    interface Listener {
        fun onScanConfirm(numbers: List<Int>)
        fun onScanRetry()
    }

    private var _binding: DialogScanResultBinding? = null
    private val binding get() = _binding!!

    private var numbers: List<Int> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        numbers = arguments?.getIntegerArrayList(ARG_NUMBERS)?.toList().orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberViews = listOf(
            binding.num1,
            binding.num2,
            binding.num3,
            binding.num4,
            binding.num5,
            binding.num6
        )

        numbers.forEachIndexed { index, number ->
            numberViews[index].text = number.toString()
        }

        binding.btnConfirm.setOnClickListener {
            (activity as? Listener)?.onScanConfirm(numbers)
            dismiss()
        }

        binding.btnRetry.setOnClickListener {
            (activity as? Listener)?.onScanRetry()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NUMBERS = "arg_numbers"

        fun newInstance(numbers: List<Int>): ScanResultDialog {
            return ScanResultDialog().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(ARG_NUMBERS, ArrayList(numbers))
                }
            }
        }
    }
}
