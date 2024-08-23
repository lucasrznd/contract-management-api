package br.com.lucasrznd.contractmanagementapi.controllers;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StandardError;
import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
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

@Tag(name = "SellerController", description = "Controller responsible for sellers operations")
@RequestMapping("/sellers")
public interface SellerController {

    @Operation(summary = "Save new seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seller created", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = SellerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    ResponseEntity<SellerResponse> save(@RequestBody @Valid final SellerRequest request);

    @Operation(summary = "Find all sellers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sellers found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SellerResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping
    ResponseEntity<List<SellerResponse>> findAll();

    @Operation(summary = "Update seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller updated", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = SellerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Seller not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @PutMapping("/{id}")
    ResponseEntity<SellerResponse> update(@Parameter(description = "Seller id", required = true, example = "1")
                                          @PathVariable(name = "id") Long id,
                                          @RequestBody @Valid UpdateSellerRequest request);

    @Operation(summary = "Delete seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Seller deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Seller not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Seller id", required = true, example = "1")
                                @PathVariable(name = "id") Long id);

}
