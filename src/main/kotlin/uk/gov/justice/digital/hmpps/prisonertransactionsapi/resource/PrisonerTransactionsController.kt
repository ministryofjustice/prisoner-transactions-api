package uk.gov.justice.digital.hmpps.prisonertransactionsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.ErrorResponse
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.PrisonerTransactionsRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.PrisonerTransactionsService
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotEmpty

@RestController
@RequestMapping( produces = [MediaType.APPLICATION_JSON_VALUE])
class PrisonerTransactionsController(private val prisonerTransactionsService: PrisonerTransactionsService) {

  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping(value = ["/link/email"])
//  @PreAuthorize("hasAnyRole('SYSTEM_USER')")
  @ResponseBody
  @Operation(
    summary = "Create and send magic link to a email",
    description = "Creates a magic link and send to the email address entered by the user.",
    security = [SecurityRequirement(name = "ROLE_SYSTEM_USER")],
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Magic link created",
        content = [
          Content(mediaType = "application/json")
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden, requires an appropriate role",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      )
    ]
  )
  fun createMagicLink(@RequestBody @NotEmpty request: PrisonerTransactionsRequest, httpReq: HttpServletRequest) {
    log.info("Remote IP Address ${httpReq.remoteAddr}")
    log.info("Remote Host ${httpReq.remoteHost}")
    prisonerTransactionsService.generateMagicLink(request)
  }
}
