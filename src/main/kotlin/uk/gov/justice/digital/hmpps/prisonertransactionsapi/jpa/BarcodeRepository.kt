package uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Repository
interface BarcodeRepository : JpaRepository<Barcode, String>

@Entity
@Table(name = "barcodes")
data class Barcode(
  @Id
  @NotNull
  val code: String = "",
)
