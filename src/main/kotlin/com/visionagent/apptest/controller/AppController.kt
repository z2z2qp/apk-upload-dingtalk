package com.visionagent.apptest.controller

import com.dingtalk.api.request.OapiRobotSendRequest
import com.visionagent.apptest.entity.App
import com.visionagent.apptest.model.request.QueryList
import com.visionagent.apptest.service.AppService
import com.visionagent.util.DingRobotUtil
import lombok.Getter
import lombok.Setter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.system.ApplicationHome
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
import org.springframework.web.servlet.ModelAndView
import tinker.net.dongliu.apk.parser.ApkParser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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
    @Value("\${app.host}")
    private val host: String = "localhost:9999"

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

    private fun save(file: File, content: String): App? {
        val apkParser = ApkParser(file)
        val packageName = apkParser.apkMeta.packageName
        val code = apkParser.apkMeta.versionCode
        val version = apkParser.apkMeta.versionName
        val name = apkParser.apkMeta.name
        val absolutePath = File(ApplicationHome(this.javaClass).source.parentFile.toString() + "/apk")
        val icon = apkParser.iconFile.data
        val f = File(absolutePath, "$packageName.png")
        val fos = FileOutputStream(f)
        fos.write(icon)
        fos.flush()
        fos.close()
        val app = App(null, packageName, file.absolutePath, version, code.toInt(), content, Date(), name, f.absolutePath)
        return if (appService.save(app).id != null) {
            app
        } else {
            null
        }
    }

    private fun send(app: App, content: String, at: List<String>): Boolean {
        val robot = DingRobotUtil(secret, webhook)
        val link = OapiRobotSendRequest.Link()
        link.title = "${app.name}(${app.version})测试"
        link.text = content
        link.messageUrl = "http://$host/app/${app.id}/apk"
        link.picUrl = "http://$host/app/${app.id}/icon"
        return robot.send(link, at)
    }

    @GetMapping("/{id}/{type}")
    fun getApk(@PathVariable id: Int, @PathVariable type: String, response: HttpServletResponse) {
        val app = appService.getAppById(id)
        if (app != null) {
            val filePath = if (type == "apk") {
                app.apk
            } else {
                app.icon
            }

            val file = File(filePath)
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
                    val app = save(file, content)
                    if (app == null) {
                        "error"
                    } else {
                        val at = request.getParameter("at").split(",")
                        if (send(app, content, at)) {
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

    @GetMapping("/list")
    fun getList(query: QueryList): ModelAndView {
        val list = appService.queryList(query)
        list.get().forEach {
            it.apk = ""
            it.icon = ""
        }
        val mv = ModelAndView()
        mv.viewName = "list"
        mv.addObject("list", list)
        return mv
    }

    @ResponseBody
    @GetMapping("/name")
    fun getName(): List<String> {
        return appService.getNames()
    }
}