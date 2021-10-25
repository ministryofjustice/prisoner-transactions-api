package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter

@Configuration
class MessageConvertersConfig {

  @Bean
  fun byteArrayHttpMessageConverter(): ByteArrayHttpMessageConverter =
    ByteArrayHttpMessageConverter().apply {
      supportedMediaTypes = mutableListOf(MediaType.IMAGE_PNG)
    }
}
