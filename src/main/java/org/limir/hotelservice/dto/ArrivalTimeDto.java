package org.limir.hotelservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ArrivalTimeDto(
        @NotNull
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "14:00")
        LocalTime checkIn,

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "12:00")
        LocalTime checkOut
) {
}
