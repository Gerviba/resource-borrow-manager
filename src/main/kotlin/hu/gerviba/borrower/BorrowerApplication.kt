package hu.gerviba.borrower

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@SpringBootApplication
class BorrowerApplication

fun main(args: Array<String>) {
    runApplication<BorrowerApplication>(*args)
}

