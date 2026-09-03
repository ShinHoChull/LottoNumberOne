package com.squirrel.lottonumberone.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.squirrel.lottonumberone.base.BaseActivity
import com.squirrel.lottonumberone.databinding.ActivityMainBinding
import com.squirrel.lottonumberone.ui.dialog.MixNumbersDialog
import com.squirrel.lottonumberone.ui.home.adapter.CameraButtonCallBackListener
import com.squirrel.lottonumberone.ui.home.adapter.MainListAdapter
import com.squirrel.lottonumberone.ui.home.adapter.PlusButtonCallBackListener
import com.squirrel.lottonumberone.ui.number_select.NumberCheckFragment
import com.squirrel.lottonumberone.ui.scan.CameraScanActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val mListAdapter: MainListAdapter by lazy {
        MainListAdapter()
    }

    private val viewModel: HomeViewModel by viewModels()

    private val cameraScanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val numbers = result.data?.getIntegerArrayListExtra(CameraScanActivity.EXTRA_NUMBERS)
            if (numbers != null && numbers.size == 6) {
                viewModel.setNumberArray(numbers.toMutableList())
            }
        }
    }

    override fun setUpInit() {
        setUpListAdapterSetting()
    }

    override fun setUpListener() {
        /**  2025-03-28
         * [HomeViewModel.numberArray]저장된 숫자를 Mix함
        */
        mBinding.mixButton1.setOnClickListener {
            viewModel.setIncludingMixNumber()
        }

        /**  2025-03-28
         * [HomeViewModel.numberArray]저장된 숫자를 제외하고 Mix함
         */
        mBinding.mixButton2.setOnClickListener {
            viewModel.setExcludingMixNumber()
        }

    }

    private fun setUpListAdapterSetting() {
        mBinding.numberListView.adapter = mListAdapter

        mListAdapter!!.setPlusCallBack(object: PlusButtonCallBackListener {
            override fun clickPlusButton() {
                /**
                 * @see [NumberCheckFragment]에서 번호를 저장하면
                 * [com.squirrel.lottonumberone.ui.home.HomeViewModel.setNumberArray]에 추가시키면
                 * [com.squirrel.lottonumberone.ui.home.MainActivity.observeViewModel]의  numberArray.observe 됨.
                 */
                val bottomView = NumberCheckFragment()
                bottomView.onNumberChecked = { numbers ->
                    viewModel.setNumberArray(numbers)
                }
                bottomView.show(supportFragmentManager, bottomView.tag)
            }
        })

        mListAdapter.setCameraCallBack(object : CameraButtonCallBackListener {
            override fun clickCameraButton() {
                cameraScanLauncher.launch(Intent(this@MainActivity, CameraScanActivity::class.java))
            }
        })
    }

    override fun observeViewModel() {

        viewModel.numberArray.observe(this) {
            if (viewModel.numberArray.value!!.size > 0) {
                mListAdapter.setLottoRow(viewModel.numberArray.value!![viewModel.numberArray.value!!.size -1])
            }
        }

        viewModel.excludingMixNumberArray.observe(this) {
            if (viewModel.excludingMixNumberArray.value!!.size > 0) {
                val dialog = MixNumbersDialog()
                val bundle = Bundle()
                bundle.putString("mixType", "exclude")
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "A")
            }
        }

        viewModel.includingMixNumberArray.observe(this) {
            if (viewModel.includingMixNumberArray.value!!.size > 0) {
                val dialog = MixNumbersDialog()
                val bundle = Bundle()
                bundle.putString("mixType", "include")
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "B")
            }
        }

    }

}