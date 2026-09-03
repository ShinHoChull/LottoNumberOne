package com.squirrel.lottonumberone.config

import android.util.Log

class Defines {
    companion object {

        public const val DEBUG : Boolean = true

        public fun log(
            log: String,
            logName: String = "YONG_CHEOL"
        ) {
            if (DEBUG) {
                Log.d(logName, "==================")
                Log.d(logName, log)
                Log.d(logName, "=====================")
            }
        }

    }
}