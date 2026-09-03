package com.squirrel.lottonumberone.ui.dialog.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squirrel.lottonumberone.R
import com.squirrel.lottonumberone.config.Defines
import com.squirrel.lottonumberone.databinding.DialogMixNumbersBinding
import com.squirrel.lottonumberone.databinding.ItemLottoRowBinding

class MixNumberAdapter(private val list: List<List<Int>>): RecyclerView.Adapter<MixNumberAdapter.NumbersViewHolder>() {

    lateinit var mBinding: ItemLottoRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumbersViewHolder {
        mBinding = ItemLottoRowBinding.inflate(LayoutInflater.from(parent.context))
        return NumbersViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: NumbersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size


    inner class NumbersViewHolder(private val mBinding: ItemLottoRowBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(row: List<Int>) {
            mBinding.numberContainer.removeAllViews()

            row.forEach { number ->

                val tv = TextView(itemView.context).apply {
                    text = number.toString()
                    textSize = 17f
                    setTextColor(Color.BLACK)
                    background = null
                    setPadding(20, 5, 20, 5)
                }
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 0, 8, 0) }
                mBinding.numberContainer.addView(tv, lp)

            }

        }


    }
}