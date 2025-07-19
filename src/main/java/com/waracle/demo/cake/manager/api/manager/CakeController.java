package com.waracle.demo.cake.manager.api.manager;

import com.waracle.demo.cake.manager.api.manager.dto.CmCakeDto;
import com.waracle.demo.cake.manager.models.manager.CmCake;
import com.waracle.demo.cake.manager.repository.manager.CmCakeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cake Management", description = "Endpoints for managing cakes")
@RestController
@RequestMapping("/cake")
public class CakeController {

    private final CmCakeRepository cakeRepository;

    public CakeController(CmCakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    @Operation(summary = "Get all cakes", description = "Returns a list of all cakes")
    @ApiResponse(responseCode = "200", description = "List of cakes",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CmCakeDto.class)))
    @GetMapping
    public ResponseEntity<List<CmCakeDto>> getAllCakes() {
        List<CmCakeDto> cakes = cakeRepository.findAll().stream()
                .map(CmCakeDto::toDto)
                .toList();
        return ResponseEntity.ok(cakes);
    }

    @Operation(summary = "Get cake by ID", description = "Returns a single cake by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cake found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CmCakeDto.class))),
            @ApiResponse(responseCode = "404", description = "Cake not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CmCakeDto> getCakeById(@PathVariable Long id) {
        return cakeRepository.findById(id)
                .map(cake -> ResponseEntity.ok(CmCakeDto.toDto(cake)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new cake", description = "Creates a new cake")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cake created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CmCakeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CmCakeDto> createCake(@Valid @RequestBody CmCakeDto cmCakeDto) {
        CmCake savedCake = cakeRepository.save(CmCakeDto.toEntity(cmCakeDto));
        return new ResponseEntity<>(CmCakeDto.toDto(savedCake), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing cake", description = "Updates a cake by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cake updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CmCakeDto.class))),
            @ApiResponse(responseCode = "404", description = "Cake not found")
    })
    @PutMapping
    public ResponseEntity<CmCakeDto> updateCake(@Valid @RequestBody CmCakeDto cmCakeDto) {
        if (!cakeRepository.existsById(cmCakeDto.id())) {
            return ResponseEntity.notFound().build();
        }
        CmCake updatedCake = cakeRepository.save(CmCakeDto.toEntity(cmCakeDto));
        return ResponseEntity.ok(CmCakeDto.toDto(updatedCake));
    }

    @Operation(summary = "Delete a cake", description = "Deletes a cake by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cake deleted"),
            @ApiResponse(responseCode = "404", description = "Cake not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
        if (!cakeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cakeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
