package uk.gov.justice.digital.hmpps.prisonertransactionsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication()
@ConfigurationPropertiesScan
class PrisonerTransactionsApi

fun main(args: Array<String>) {
  runApplication<PrisonerTransactionsApi>(*args)
}
