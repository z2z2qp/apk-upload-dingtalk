package com.visionagent.apptest.entity

import java.util.*
import javax.persistence.*

/**
 * @Description
 * @Author HX
 * @Date 2020/2/21 9:06
 * @Version 1.0
 */

@Entity
@Table(name = "app", schema = "app")
data class App(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int?,
        var packageName: String,
        var apk: String,
        var version: String,
        var code: Int,
        var content: String,
        var createTime: Date?)