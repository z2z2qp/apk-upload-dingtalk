package com.visionagent.apptest.repository

import com.visionagent.apptest.entity.App
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * @Description
 * @Author HX
 * @Date 2020/2/21 9:52
 * @Version 1.0
 */
interface AppRepository : JpaRepository<App, Int> {

    fun findAppByPackageNameAndCode(packageName: String, code: Int): App?

    fun findAllByName(name: String, pageable: Pageable): Page<App>

    @Query("select distinct name from app",nativeQuery = true)
    fun findNames(): List<String>
}