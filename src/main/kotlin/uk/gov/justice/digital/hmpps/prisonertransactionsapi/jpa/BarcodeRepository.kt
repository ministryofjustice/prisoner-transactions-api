package uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa

import org.hibernate.Hibernate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Repository
interface BarcodeRepository : JpaRepository<Barcode, Long> {
  fun findByBarcode(barcode: String): Barcode?
}

@Entity
@Table(name = "barcodes")
data class Barcode(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull
  val id: Long = -1,
  @NotNull
  @Column(unique = true)
  val barcode: String,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as Barcode

    return barcode == other.barcode
  }

  override fun hashCode(): Int = barcode.hashCode()

  override fun toString(): String {
    return "Barcode(barcode='$barcode')"
  }
}
