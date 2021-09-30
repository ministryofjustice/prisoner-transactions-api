package uk.gov.justice.digital.hmpps.prisonertransactionsapi.email

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailSender(
  private val javaMailSender: JavaMailSender,
  @Value("\${app.magiclink.url}") private val magicLinkUrl: String,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendEmail(email: String, secret: String) {
    log.info("Sending email....")

    val message = SimpleMailMessage()

    message.setTo(email)
    message.subject = "prisoner transaction login"
    message.text = magicLinkUrl + secret
    log.info("Email body - ${message.text}")

    javaMailSender.send(message)
    log.info("Email sent to - $email")
  }
}
