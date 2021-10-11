package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

@Service
class JwtService(private val jwtKeyProvider: JwtKeyProvider, @Value("\${jwt.expiry}") private val jwtExpiry: Duration) {

  val log = LoggerFactory.getLogger(this::class.java)

  fun generateToken(email: String) =
    Jwts.builder()
      .setId(UUID.randomUUID().toString())
      .setSubject(email)
      .setExpiration(
        Date.from(
          LocalDateTime.now().plus(jwtExpiry.toMillis(), ChronoUnit.MILLIS).toInstant(zoneOffset())
        )
      )
      .signWith(SignatureAlgorithm.RS256, jwtKeyProvider.privateKey)
      .compact()

  private fun zoneOffset() = ZoneId.systemDefault().getRules().getOffset(
    Instant.now()
  )

  fun validateToken(jwt: String): Boolean =
    runCatching {
      Jwts.parser().setSigningKey(jwtKeyProvider.publicKey).parseClaimsJws(jwt)
    }.onFailure {
      log.warn("Found an invalid JWT: jwt", it)
    }.isSuccess

  fun subject(jwt: String): String =
    Jwts.parser().setSigningKey(jwtKeyProvider.publicKey).parseClaimsJws(jwt).body.subject
}
