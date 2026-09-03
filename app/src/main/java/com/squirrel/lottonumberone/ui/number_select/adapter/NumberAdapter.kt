package com.squirrel.lottonumberone.ui.number_select.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squirrel.lottonumberone.R
import com.squirrel.lottonumberone.databinding.ItemNumberCheckBinding
import com.squirrel.lottonumberone.ui.number_select.NumberViewModel

class NumberAdapter(
    val context: Context,
    val viewModel: NumberViewModel
): RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    private lateinit var mBinding: ItemNumberCheckBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        mBinding = ItemNumberCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 45
    }

    inner class NumberViewHolder(private val mBinding: ItemNumberCheckBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(position: Int) {
            val number = position + 1
            mBinding.number.text = number.toString()

            val isSelected = viewModel.numberArray.value?.contains(number) == true
            mBinding.number.setBackgroundResource(
                if (isSelected) R.drawable.bg_number_selected else R.drawable.bg_number_default
            )

            mBinding.number.setOnClickListener {
                val selectedNumbers = viewModel.numberArray.value ?: return@setOnClickListener

                if (!selectedNumbers.contains(number)) {
                    if (selectedNumbers.size >= 6) {
                        Toast.makeText(context, "6개 이상 선택 하실 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.setNumberArray(number)
                    mBinding.number.setBackgroundResource(R.drawable.bg_number_selected)
                } else {
                    selectedNumbers.remove(number)
                    mBinding.number.setBackgroundResource(R.drawable.bg_number_default)
                }
            }
        }
    }
}