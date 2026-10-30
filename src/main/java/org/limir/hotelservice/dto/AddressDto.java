package org.limir.hotelservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDto(
        @NotNull Integer houseNumber,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String country,
        @NotBlank String postCode
) {
}
