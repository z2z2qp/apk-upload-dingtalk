package com.visionagent.apptest.controller

import com.visionagent.apptest.model.WXMessage
import com.visionagent.apptest.util.WeChatUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/wx")
class WeChatController {

    @Resource
    private lateinit var utils: WeChatUtils

    @GetMapping("/core")
    fun doGet(signature:String, timestamp:String , nonce:String , echostr:String , response: HttpServletResponse){

        val pw = response.writer
        if (utils.checkSignature(signature,timestamp,nonce)){
            pw.print(echostr)
        }
        pw.flush()
    }

    @PostMapping("/core")
    fun doPost(request: HttpServletRequest,response: HttpServletResponse){
        val message = WXMessage()
        val r = request.reader
        val sb = StringBuilder()
        r.lines().forEach { sb.append(it) }
        println(sb)
        val pw = response.writer
        pw.print("success")
        pw.flush()
    }
}