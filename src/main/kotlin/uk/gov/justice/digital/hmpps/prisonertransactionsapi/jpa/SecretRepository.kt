package uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SecretRepository : CrudRepository<Secret, String>

@RedisHash(value = "sessionSecrets")
data class Secret(
  @Id
  val email: String,
  val sessionId: String,
  val secretValue: String,
)
