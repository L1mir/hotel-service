package org.limir.hotelservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.limir.hotelservice.dto.HotelCreateRequest;
import org.limir.hotelservice.dto.HotelDetailsResponse;
import org.limir.hotelservice.dto.HotelShortResponse;
import org.limir.hotelservice.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    public List<HotelShortResponse> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotels/{id}")
    public HotelDetailsResponse getHotel(@PathVariable Long id) {
        return hotelService.getHotel(id);
    }

    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelShortResponse createHotel(@Valid @RequestBody HotelCreateRequest request) {
        return hotelService.createHotel(request);
    }

    @PostMapping("/hotels/{id}/amenities")
    public void addAmenities(@PathVariable Long id, @RequestBody List<@NotBlank String> amenities) {
        hotelService.addAmenities(id, amenities);
    }
}
