package org.limir.hotelservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelCreateRequest(
        @NotBlank String name,
        String description,
        @NotBlank String brand,
        @NotNull @Valid AddressDto address,
        @NotNull @Valid ContactsDto contacts,
        @NotNull @Valid ArrivalTimeDto arrivalTime
) {
}
