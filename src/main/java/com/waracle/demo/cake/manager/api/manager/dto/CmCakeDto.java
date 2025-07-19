package com.waracle.demo.cake.manager.api.manager.dto;

import com.waracle.demo.cake.manager.models.manager.CmCake;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for Cake")
public record CmCakeDto(
        @Schema(description = "Cake ID", example = "1")
        Long id,

        @Schema(description = "Cake title", example = "Chocolate Cake")
        @NotBlank
        String title,

        @Schema(description = "Cake description", example = "Rich chocolate sponge with ganache")
        String description,

        @Schema(description = "Image URL", example = "https://example.com/cake.jpg")
        String image
) {

    public static CmCakeDto toDto(CmCake cake) {
        return new CmCakeDto(cake.getId(), cake.getTitle(), cake.getDescription(), cake.getImage());
    }

    public static CmCake toEntity(CmCakeDto dto) {
        return new CmCake(dto.id(), dto.title(), dto.description(), dto.image());
    }
}