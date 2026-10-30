package org.limir.hotelservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.limir.hotelservice.dto.HotelCreateRequest;
import org.limir.hotelservice.dto.HotelDetailsResponse;
import org.limir.hotelservice.dto.HotelSearchCriteria;
import org.limir.hotelservice.dto.HotelShortResponse;
import org.limir.hotelservice.service.HotelService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Hotels")
@RestController
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @Operation(summary = "Get all hotels with short information")
    @GetMapping("/hotels")
    public List<HotelShortResponse> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @Operation(summary = "Get detailed information about a hotel")
    @GetMapping("/hotels/{id}")
    public HotelDetailsResponse getHotel(@PathVariable Long id) {
        return hotelService.getHotel(id);
    }

    @Operation(summary = "Create a new hotel")
    @PostMapping("/hotels")
    public HotelShortResponse createHotel(@Valid @RequestBody HotelCreateRequest request) {
        return hotelService.createHotel(request);
    }

    @Operation(summary = "Add amenities to a hotel")
    @PostMapping("/hotels/{id}/amenities")
    public void addAmenities(@PathVariable Long id, @RequestBody List<@NotBlank String> amenities) {
        hotelService.addAmenities(id, amenities);
    }

    @Operation(summary = "Search hotels by name, brand, city, country and amenities")
    @GetMapping("/search")
    public List<HotelShortResponse> searchHotels(@ParameterObject HotelSearchCriteria criteria) {
        return hotelService.searchHotels(criteria);
    }

    @Operation(summary = "Get the number of hotels grouped by the given parameter")
    @GetMapping("/histogram/{param}")
    public Map<String, Long> getHistogram(
            @Parameter(schema = @Schema(allowableValues = {"brand", "city", "country", "amenities"}))
            @PathVariable String param) {
        return hotelService.getHistogram(param);
    }
}
