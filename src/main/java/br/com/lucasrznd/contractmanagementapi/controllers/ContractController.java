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

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
