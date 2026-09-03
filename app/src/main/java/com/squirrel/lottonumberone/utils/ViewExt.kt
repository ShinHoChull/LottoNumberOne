package com.squirrel.lottonumberone.utils


import android.app.Activity
import android.app.Service
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.squirrel.lottonumberone.config.Defines
import java.text.SimpleDateFormat
import java.util.*

fun View.showKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}


fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toInvisible() {
    this.visibility = View.GONE
}

fun View.showToast(
    lifecycleOwner: LifecycleOwner,
    toastEvent: LiveData<SingleEvent<Any>>,
    timeLength: Int
) {
    toastEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            when (it) {
                is String -> Toast.makeText(this.context, it, timeLength).show()
                is Int -> Toast.makeText(this.context, this.context.getString(it), timeLength)
                    .show()
                else -> {}
            }
        }
    })
}

fun View.showSnackBar(
    lifecycleOwner: LifecycleOwner,
    view: View,
    snackBarEvent: LiveData<SingleEvent<Any>>,
    timeLength: Int
) {
    snackBarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            when (it) {
                is String -> Snackbar.make(this.context, view, it, timeLength).show()
                is Int -> Snackbar.make(this.context, view, this.context.getString(it), timeLength).show()
            }
        }
    })
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.upperChangeText(afterTextChange: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(p0: Editable?) {

            if (p0.toString() == p0.toString().uppercase()) return
            afterTextChange.invoke(p0.toString().uppercase())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}

fun EditText.focusClearChange() {
    this.inputType = InputType.TYPE_NULL
    this.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
        if (p1) {
            if (this.text.toString() != "") {
                this.setText("")
            }
            Defines.log("focus")
        } else {
            Defines.log("not focus")
        }
    }
}

fun EditText.focusChange() {
    this.inputType = InputType.TYPE_NULL
    this.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
        if (p1) {
            Defines.log("focus")
        } else {
            Defines.log("not focus")
        }
    }
}

fun EditText.enterActionChange() {
    this.setOnKeyListener { _, i, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
            if (i == KeyEvent.KEYCODE_ENTER) {
                Defines.log("enter")
                this.setText("a")
            }
        }
        return@setOnKeyListener true
    }
}

fun EditText.filedNullCheck(): Boolean = text.toString().trim() == ""
fun String.filedNullCheck(): Boolean = this.trim() == ""

fun Activity.hideSoftKeyboard() {
    val inputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = this.currentFocus
    val windowToken = this.window?.decorView?.rootView?.windowToken
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    inputMethodManager.hideSoftInputFromWindow(
        windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
    if (currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}

fun Date.getDate(pattern: String?): String? {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.KOREA)
    return simpleDateFormat.format(Date())
}

fun AppCompatTextView.setTextFutureExt(text: String) =
    setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )

fun AppCompatEditText.setTextFutureExt(text: String) =
    setText(
        PrecomputedTextCompat.create(text, TextViewCompat.getTextMetricsParams(this))
    )

