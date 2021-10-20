package uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa

import org.hibernate.Hibernate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Repository
interface BarcodeEventRepository : JpaRepository<BarcodeEvent, Long> {
  fun findByBarcode(barcode: Barcode): List<BarcodeEvent>
}

@Entity
@Table(name = "barcode_events")
data class BarcodeEvent(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull
  val id: Long = -1,
  @NotNull
  @ManyToOne
  @JoinColumn(name = "BARCODE")
  val barcode: Barcode,
  @NotNull
  val userId: String,
  @NotNull
  val prison: String,
  @NotNull
  val prisonerId: String,
  @NotNull
  val status: BarcodeStatus = BarcodeStatus.CREATED,
  @NotNull
  val dateTime: LocalDateTime = LocalDateTime.now(),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as BarcodeEvent

    return barcode == other.barcode && dateTime == other.dateTime
  }

  override fun hashCode(): Int = barcode.hashCode() * dateTime.hashCode()

  override fun toString(): String {
    return "BarcodeEvent(barcode=$barcode, userId='$userId', prison='$prison', prisonerId='$prisonerId', status=$status, dateTime=$dateTime)"
  }
}

enum class BarcodeStatus { CREATED, SCANNED }