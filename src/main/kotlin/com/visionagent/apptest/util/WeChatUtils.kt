package com.visionagent.apptest.util

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*


@Slf4j
@Component
class WeChatUtils {

    @Value("\${app.wx.token}")
    private lateinit var token:String

    @Value("\${app.wx.key}")
    private lateinit var key:String

    fun checkSignature(signature:String,timestamp:String,nonce:String):Boolean{
        val arr = arrayOf(token,timestamp,nonce)
        Arrays.sort(arr)
        val sb = StringBuilder()
        arr.forEach { sb.append(it) }

        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(sb.toString().toByteArray())
        val tmpStr = byteArrayToHexString(digest)
        return tmpStr != null && tmpStr.equals(signature,true)
    }

    private fun byteArrayToHexString(src:ByteArray?):String?{
        if (src == null || src.isEmpty()){
            return null
        }
        val sb = StringBuilder()
        for (i in src){
            val hv = Integer.toHexString(i.toInt().and(0xFF))
            if (hv.length<2){
                sb.append(0)
            }
            sb.append(hv)
        }
        return sb.toString()
    }
}