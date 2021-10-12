package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Component
class JwtKeyProvider(
  @Value("\${jwt.private.key}") privateKeyString: String,
  @Value("\${jwt.public.key}") publicKeyString: String
) {

  val privateKey: PrivateKey = readPrivateKey(privateKeyString)
  val publicKey: PublicKey = readPublicKey(publicKeyString)

  private fun readPrivateKey(privateKeyString: String): PrivateKey =
    PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString))
      .let { KeyFactory.getInstance("RSA").generatePrivate(it) }

  private fun readPublicKey(publicKeyString: String): PublicKey =
    X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString))
      .let { KeyFactory.getInstance("RSA").generatePublic(it) }
}
