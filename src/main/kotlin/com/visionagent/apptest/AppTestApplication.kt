package com.visionagent.apptest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppTestApplication

fun main(args: Array<String>) {
    runApplication<AppTestApplication>(*args)
}
