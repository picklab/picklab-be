package picklab.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PickLabApplication

fun main(args: Array<String>) {
    runApplication<PickLabApplication>(*args)
}
