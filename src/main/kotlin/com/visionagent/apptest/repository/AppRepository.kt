package com.visionagent.apptest.repository

import com.visionagent.apptest.entity.App
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @Description
 * @Author HX
 * @Date 2020/2/21 9:52
 * @Version 1.0
 */
interface AppRepository : JpaRepository<App, Int>{

    fun findAppByPackageNameAndCode(packageName:String,code:Int):App?
}