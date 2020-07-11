package com.visionagent.apptest.service

import com.visionagent.apptest.entity.App
import com.visionagent.apptest.model.request.QueryList
import com.visionagent.apptest.repository.AppRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

/**
 * @Description
 * @Author HX
 * @Date 2020/2/21 9:50
 * @Version 1.0
 */

@Service
class AppService {
    @Autowired
    lateinit var appRepository: AppRepository

    /**
     * 保存
     * @param app 待保存app对象
     * @return App 保存后的对象
     */
    fun save(app: App): App {
        return appRepository.save(app)
    }

    /**
     * 根据包名 code获取 app
     * @param packageName 包名
     * @param code code
     * @return 获取到的App 可能为空
     */
    fun getByPackageNameAndCode(packageName: String, code: Int): App? {
        return appRepository.findAppByPackageNameAndCode(packageName, code)
    }

    /**
     * 更具id 获取 App
     * @param id id
     * @return 获取到的App 可能为空
     */
    fun getAppById(id: Int): App? {
        return appRepository.getOne(id)
    }

    /**
     * 列表分页查询
     * @param query 查询参数对象
     * @return 分页 App
     */
    fun queryList(query: QueryList): Page<App> {
        val page = if (query.page != null) {
            query.page!! - 1
        } else {
            0
        }
        val rows = if (query.rows == null) {
            10
        } else {
            query.rows
        }
        val pageable = PageRequest.of(page, rows!!)
        return if (query.name == null) {
            appRepository.findAll(pageable)
        } else {
            appRepository.findAllByName(query.name!!, pageable)
        }
    }

    /**
     * 获取所以app名称
     * @return app 名称 List
     */
    fun getNames(): List<String> {
        return appRepository.findNames()
    }
}