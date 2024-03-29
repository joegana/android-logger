package com.justin.test

import android.app.Activity
import android.os.Bundle
import com.justin.timber.Timber
import com.moorgen.android.log.LoggerManager
import com.moorgen.android.log4j.LogConfigurator
import org.slf4j.Logger
import java.io.File

/**
 * Created by admin on 2018/7/23.
 */
class MainActivity:Activity() {
    private val Log = LoggerManager.getLogger(MainActivity::class.java)
    private var logger:Logger? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configLog4Android(true)
        Log.i("%s","就")
//        Timber.plant(Timber.DebugTree())
        Timber.d("Timer log 测试")
        Log.d("%d",1)
        Log.v("%s",1)
        Log.w("%b",0)
        Log.e("%就")
        Log.a("%就")
    }


    private fun configLog4Android(enableDiskLog: Boolean) {
        val logConfigurator = LogConfigurator()
        val file = File(this.externalCacheDir,"test.log")
        val path = file.path
        logConfigurator.setFileName(path)
        logConfigurator.setRootLevel(org.apache.log4j.Level.ALL)
        logConfigurator.setLevel("org.apache", org.apache.log4j.Level.ALL)
        logConfigurator.setUseFileAppender(enableDiskLog)
        logConfigurator.setFilePattern("%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p -[%t] [%c{2}]-[%L] %m%n")
        logConfigurator.setLogCatPattern("%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{2}]-[%L] %m%n")
        logConfigurator.setMaxBackupSize(21)
        logConfigurator.setMaxFileSize(1024 * 1024 * 2)
        logConfigurator.setImmediateFlush(true)
        logConfigurator.configure()
        logger = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)
        logger?.info("LogFilePath = {} ”", path)
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