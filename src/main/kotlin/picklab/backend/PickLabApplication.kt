package picklab.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PickLabApplication

fun main(args: Array<String>) {
    runApplication<PickLabApplication>(*args)
}
