package org.limir.hotelservice.dto;

public record HotelShortResponse(
        Long id,
        String name,
        String description,
        String address,
        String phone
) {
}
