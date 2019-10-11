package net.relaxism.devtools.webdevtools

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebDevToolsApplication

fun main(args: Array<String>) {
    runApplication<WebDevToolsApplication>(*args)
}
