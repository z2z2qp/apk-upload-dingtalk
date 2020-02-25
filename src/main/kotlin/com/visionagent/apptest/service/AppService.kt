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

    fun save(app: App): App {
        return appRepository.save(app)
    }

    fun getByPackageNameAndCode(packageName: String, code: Int): App? {
        return appRepository.findAppByPackageNameAndCode(packageName, code)
    }

    fun getAppById(id: Int): App? {
        return appRepository.getOne(id)
    }

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
        val pageable = PageRequest.of(page!!, rows!!)
        return if (query.name == null) {
            appRepository.findAll(pageable)
        } else {
            appRepository.findAllByName(query.name!!, pageable)
        }
    }

    fun getNames(): List<String> {
        return appRepository.findNames()
    }
}