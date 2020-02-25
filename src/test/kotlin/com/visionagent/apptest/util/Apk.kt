package com.visionagent.apptest.util

import org.junit.jupiter.api.Test
import tinker.net.dongliu.apk.parser.ApkParser
import java.io.File
import java.io.FileOutputStream

class Apk {

    @Test
    fun icon(){
        val apk = ApkParser("E:\\Downloads\\H5449AB8B_0116111230.apk")
        val icon = apk.iconFile.data
        val fos = FileOutputStream(File("icon.png"))
        fos.write(icon)
        fos.flush()
        fos.close()
    }
}