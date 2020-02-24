package com.visionagent.apptest.controller

import com.dingtalk.api.request.OapiRobotSendRequest
import com.visionagent.apptest.entity.App
import com.visionagent.apptest.service.AppService
import com.visionagent.util.DingRobotUtil
import lombok.Getter
import lombok.Setter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.system.ApplicationHome
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
import tinker.net.dongliu.apk.parser.ApkParser
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @Description
 * @Author HX
 * @Date 2020/2/21 9:10
 * @Version 1.0
 */

@Getter
@Setter
@Controller
@RequestMapping("/app")
class AppController {

    @Resource(name = "appService")
    lateinit var appService: AppService

    @Value("\${app.secret}")
    private val secret: String? = null
    @Value("\${app.webhook}")
    private val webhook: String? = null

    @Value("\${server.port}")
    private var port: Int = 8080

    private fun writeFile(file: MultipartFile): File? {
        return if (file.isEmpty) {
            null
        } else {
            val absolutePath = File(ApplicationHome(this.javaClass).source.parentFile.toString() + "/apk")
            if (absolutePath.exists().not()) {
                absolutePath.mkdirs()
            }
            val f = File(absolutePath, file.originalFilename!!)
            file.transferTo(f)
            f
        }
    }

    private fun save(file: File, content: String): ApkParser? {
        val apkParser = ApkParser(file)
        val packageName = apkParser.apkMeta.packageName
        val code = apkParser.apkMeta.versionCode
        val version = apkParser.apkMeta.versionName
        val app = App(null, packageName, file.absolutePath, version, code.toInt(), content, Date())
        return if (appService.save(app).id != null) {
            apkParser
        } else {
            null
        }
    }

    private fun getIP(): String {
        return Inet4Address.getLocalHost().hostAddress
    }

    private fun send(apkParser: ApkParser, content: String, at: List<String>): Boolean {
        val robot = DingRobotUtil(secret, webhook)
        val link = OapiRobotSendRequest.Link()
        link.title = "${apkParser.apkMeta.name}(${apkParser.apkMeta.versionName})测试"
        link.text = content
        link.messageUrl = "http://${getIP()}:$port/app/${apkParser.apkMeta.packageName}/${apkParser.apkMeta.versionCode}"
        return robot.sendAt(link, at)
    }

    @GetMapping("/{packageName}/{code}")
    fun getApk(@PathVariable packageName: String, @PathVariable code: Int, response: HttpServletResponse) {
        val app = appService.getByPackageNameAndCode(packageName, code)
        val apk = app.apk
        val file = File(apk)
        if (file.exists()) {
            val fis = FileInputStream(file)
            response.setHeader("Content-Disposition", "attachment;filename=${file.name}")
            val os = response.outputStream
            val byteArray = ByteArray(fis.available())
            fis.read(byteArray)
            fis.close()
            os.write(byteArray)
        }
    }

    @PostMapping("/upload")
    fun upload(request: HttpServletRequest): String {
        if (request is AbstractMultipartHttpServletRequest) {
            val content = request.getParameter("content")
            val files = request.multiFileMap
            if (files.size > 0) {
                val file = writeFile(files.getFirst("apk")!!)
                return if (file == null) {
                    "error"
                } else {
                    val apkParser = save(file, content)
                    if (apkParser == null) {
                        "error"
                    } else {
                        val at = request.getParameter("at").split(",")
                        if (send(apkParser, content, at)) {
                            "success"
                        } else {
                            "error"
                        }
                    }
                }
            } else {
                return "error"
            }
        } else {
            return "error"
        }
    }
}