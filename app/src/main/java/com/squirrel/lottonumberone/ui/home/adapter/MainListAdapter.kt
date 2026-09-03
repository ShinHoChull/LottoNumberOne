package com.squirrel.lottonumberone.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.squirrel.lottonumberone.config.Defines
import com.squirrel.lottonumberone.config.Etc
import com.squirrel.lottonumberone.databinding.ItemMainListBinding
import com.squirrel.lottonumberone.databinding.ItemMainListPlusButtonBinding

interface PlusButtonCallBackListener {
    fun clickPlusButton()
}

interface CameraButtonCallBackListener {
    fun clickCameraButton()
}


class MainListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mBinding: ItemMainListBinding
    private lateinit var mBindingPlusButton: ItemMainListPlusButtonBinding
    private var lottoArray: MutableList<MutableList<Int>> = mutableListOf()

    var plusCallBackListener: PlusButtonCallBackListener? = null
    var cameraCallBackListener: CameraButtonCallBackListener? = null

    fun setPlusCallBack(listener: PlusButtonCallBackListener) {
        this.plusCallBackListener = listener
    }

    fun setCameraCallBack(listener: CameraButtonCallBackListener) {
        this.cameraCallBackListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == Etc.ViewType.ZERO_CONTENT) {
            mBindingPlusButton = ItemMainListPlusButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MainListPlusButtonViewHolder(mBindingPlusButton)
        }

        mBinding = ItemMainListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainListViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is MainListPlusButtonViewHolder) {
            holder.bind()
        } else if (holder is MainListViewHolder) {
            holder.bind(lottoArray[position])
        }
    }

    override fun getItemCount(): Int {
        if (lottoArray == null || lottoArray.size == 0) {
            return 1
        }

        return lottoArray.size
    }

    fun setLottoRow(row: MutableList<Int>) {
        if (lottoArray.size == 0) {
            lottoArray.add(mutableListOf())
        }

        lottoArray.add(row)

        notifyItemRangeInserted(lottoArray.size - 1, lottoArray.size)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return Etc.ViewType.ZERO_CONTENT
        }
        return Etc.ViewType.MAIN_CONTENT
    }

    inner class MainListViewHolder(private val mBinding: ItemMainListBinding):
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(rows: MutableList<Int>) {
            rows.sort()
            mBinding.num1.text = rows[0].toString()
            mBinding.num2.text = rows[1].toString()
            mBinding.num3.text = rows[2].toString()
            mBinding.num4.text = rows[3].toString()
            mBinding.num5.text = rows[4].toString()
            mBinding.num6.text = rows[5].toString()

        }
    }

    inner class MainListPlusButtonViewHolder(val mBinding: ItemMainListPlusButtonBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind() {
            mBinding.plusButton.setOnClickListener {
                try {
                    plusCallBackListener?.clickPlusButton()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            mBinding.cameraButton.setOnClickListener {
                try {
                    cameraCallBackListener?.clickCameraButton()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}