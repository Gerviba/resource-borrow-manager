package hu.gerviba.borrower

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BorrowerApplication

fun main(args: Array<String>) {
    runApplication<BorrowerApplication>(*args)
}
