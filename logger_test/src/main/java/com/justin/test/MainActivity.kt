package com.justin.test

import android.app.Activity
import android.os.Bundle
import com.justin.timber.Timber
import com.noveogroup.android.log.LoggerManager

/**
 * Created by admin on 2018/7/23.
 */
class MainActivity:Activity() {
    private val Log = LoggerManager.getLogger(MainActivity::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("%s","就")
//        Timber.plant(Timber.DebugTree())
        Timber.d("Timer log 测试")
        Log.d("%就")
        Log.v("%就")
        Log.w("%就")
        Log.e("%就")
        Log.a("%就")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}