package br.com.lucasrznd.contractmanagementapi.controllers;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StandardError;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Tag(name = "CompanyController", description = "Controller responsible for companies operations")
@RequestMapping("/companies")
public interface CompanyController {

    @Operation(summary = "Save new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "413", description = "Image too large", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            ))
    })
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CompanyResponse> save(@ModelAttribute @Valid final CompanyRequest request);

    @Operation(summary = "Find all companies (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping
    ResponseEntity<Page<CompanyResponse>> findAll(@Parameter(description = "Text to search by business name, trade name, CNPJ, CPF or city (case and accent insensitive)")
                                          @RequestParam(name = "text", required = false) String text,
                                          @ParameterObject @PageableDefault(sort = "id") Pageable pageable);

    @Operation(summary = "Returns quantity of companies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity found", content = @Content(
                    mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @GetMapping("/count")
    ResponseEntity<Integer> countCompanies();

    @Operation(summary = "Find company by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company founded", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<CompanyResponse> findById(@Parameter(description = "Company id", required = true, example = "1")
                                             @PathVariable(name = "id") Long id);

    @Operation(summary = "Update company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            )),
            @ApiResponse(responseCode = "413", description = "Image too large", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)
            ))
    })
    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CompanyResponse> update(@Parameter(description = "Company id", required = true, example = "1")
                                           @PathVariable(name = "id") Long id,
                                           @ModelAttribute @Valid UpdateCompanyRequest request);

    @Operation(summary = "Delete company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardError.class))))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Company id", required = true, example = "1")
                                @PathVariable(name = "id") Long id);

}
