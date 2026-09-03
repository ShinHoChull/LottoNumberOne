package com.squirrel.lottonumberone.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B: ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
): AppCompatActivity() {

    private var _binding: B? = null
    val mBinding get() = _binding!!

    abstract fun setUpInit()
    protected abstract fun setUpListener()
    abstract fun observeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(mBinding?.root)
        setUpInit()
        observeViewModel()
        setUpListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}