package uk.gov.justice.digital.hmpps.prisonertransactionsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.ErrorResponse
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.MagicLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.PrisonerTransactionsService
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotEmpty

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class PrisonerTransactionsController(private val prisonerTransactionsService: PrisonerTransactionsService) {

  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping(value = ["/link/email"])
  @ResponseBody
  @Operation(
    summary = "Create and send magic link to a email",
    description = "Creates a magic link and send to the email address entered by the user.",
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
    ]
  )
  fun createMagicLink(@RequestBody @NotEmpty request: MagicLinkRequest, httpReq: HttpServletRequest) {
    log.info("Remote IP Address ${httpReq.remoteAddr}")
    log.info("Remote Host ${httpReq.remoteHost}")
    prisonerTransactionsService.generateMagicLink(request)
  }

  @PostMapping(value = ["/link/verify"])
  @ResponseBody
  @Operation(
    summary = "Verifies a magic link and returns a token if ok",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Magic link verified",
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
        responseCode = "404",
        description = "Not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      )
    ]
  )
  fun verifyMagicLink(@RequestBody @NotEmpty request: VerifyLinkRequest, httpReq: HttpServletRequest): String =
    prisonerTransactionsService.verifyMagicLink(request)
}
