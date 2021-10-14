package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.core.convert.KeyspaceConfiguration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Secret
import java.time.Duration

// we want keyspace notifications, but have to empty the config parameter (default Ex) since elasticache doesn't support
// changing the config.  If we move off elasticache then need to remove the config parameter and let it use the default.
@Configuration
@EnableRedisRepositories(
  enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
  keyspaceNotificationsConfigParameter = "\${application.keyspace-notifications:}",
  keyspaceConfiguration = SecretKeyspaceConfiguration::class
)
class RedisConfig

class SecretKeyspaceConfiguration(@Value("\${secret.expiry}") private val ttl: Duration) : KeyspaceConfiguration() {
  override fun hasSettingsFor(type: Class<*>?): Boolean {
    return true
  }

  override fun getKeyspaceSettings(type: Class<*>?): KeyspaceSettings {
    val keyspaceSettings = KeyspaceSettings(Secret::class.java, "MyHashlog")
    keyspaceSettings.timeToLive = ttl.toSeconds()
    return keyspaceSettings
  }
}
