package org.limir.hotelservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ArrivalTimeDto(
        @NotNull @JsonFormat(pattern = "HH:mm") LocalTime checkIn,
        @JsonFormat(pattern = "HH:mm") LocalTime checkOut
) {
}
