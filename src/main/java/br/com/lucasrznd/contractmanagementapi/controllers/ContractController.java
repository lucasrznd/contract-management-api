package br.com.lucasrznd.contractmanagementapi.controllers;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StandardError;
import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.DocResponse;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.*;

@Tag(name = "ContractController", description = "Controller responsible for contracts operations")
@RequestMapping("/contracts")
public interface ContractController {

    @Operation(summary = "Save new contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contract created", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContractResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            ))
    })
    @PostMapping
    ResponseEntity<ContractResponse> save(@RequestBody @Valid final ContractRequest request);

    @Operation(summary = "Find all contracts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ContractResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping
    ResponseEntity<List<ContractResponse>> findAll();

    @Operation(summary = "Find contracts that will expire in 7 days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ContractResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping("/expiration-next-week")
    ResponseEntity<List<ContractResponse>> findAllByExpirationNextWeek();

    @Operation(summary = "Find last five contracts ordered by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ContractResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping("/last-5")
    ResponseEntity<List<ContractResponse>> findLastFive();

    @Operation(summary = "Find filtered contracts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ContractResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping("/list")
    ResponseEntity<List<ContractResponse>> list(@Parameter(description = "Start Date", example = "2024-08-01")
                                                @RequestParam(name = "startDate") LocalDate startDate,
                                                @Parameter(description = "End Date", example = "2024-09-01")
                                                @RequestParam(name = "endDate") LocalDate endDate,
                                                @Parameter(description = "Company Id", example = "1")
                                                @RequestParam(name = "companyId", required = false) Long companyId,
                                                @Parameter(description = "Seller Id", example = "1")
                                                @RequestParam(name = "sellerId", required = false) Long sellerId);

    @Operation(summary = "Returns quantity of contracts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity found", content = @Content(
                    mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @GetMapping("/count")
    ResponseEntity<Integer> countContracts();

    @Operation(summary = "Returns total estimated revenue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total Estimated Revenue found", content = @Content(
                    mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @GetMapping("/total-estimated-revenue")
    ResponseEntity<String> totalEstimatedRevenue();

    @Operation(summary = "Get PDF by contract id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF founded", content = @Content(
                    mediaType = APPLICATION_PDF_VALUE, schema = @Schema(implementation = byte[].class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @GetMapping("/{id}/pdf")
    ResponseEntity<byte[]> getPDF(@Parameter(description = "Contract id", required = true, example = "1")
                                  @PathVariable(name = "id") Long id);

    @Operation(summary = "Create digital document with external lib")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document created"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @PutMapping("/{id}/create-doc")
    ResponseEntity<ContractResponse> createDigitalDoc(@Parameter(description = "Contract id", required = true, example = "1")
                                                      @PathVariable(name = "id") Long id);

    @Operation(summary = "Get document by token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document founded"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @GetMapping("/get-doc")
    ResponseEntity<DocResponse> getDocByToken(@Parameter(description = "Doc Token", required = true)
                                              @RequestParam(name = "token") String token);

    @Operation(summary = "Update contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract updated", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContractResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Contract not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            ))
    })
    @PutMapping("/{id}")
    ResponseEntity<ContractResponse> update(@Parameter(description = "Contract id", required = true, example = "1")
                                            @PathVariable(name = "id") Long id,
                                            @RequestBody @Valid UpdateContractRequest request);

    @Operation(summary = "Delete contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contract deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Contract not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Contract id", required = true, example = "1")
                                @PathVariable(name = "id") Long id);

}
