package org.limir.hotelservice.service;

import lombok.RequiredArgsConstructor;
import org.limir.hotelservice.dto.HotelCreateRequest;
import org.limir.hotelservice.dto.HotelDetailsResponse;
import org.limir.hotelservice.dto.HotelShortResponse;
import org.limir.hotelservice.entity.Hotel;
import org.limir.hotelservice.exception.HotelNotFoundException;
import org.limir.hotelservice.mapper.HotelMapper;
import org.limir.hotelservice.repository.HotelRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public List<HotelShortResponse> getAllHotels() {
        return hotelRepository.findAll(Sort.by("id")).stream()
                .map(hotelMapper::toShortResponse)
                .toList();
    }

    public HotelDetailsResponse getHotel(Long id) {
        return hotelMapper.toDetailsResponse(findHotel(id));
    }

    @Transactional
    public HotelShortResponse createHotel(HotelCreateRequest request) {
        Hotel hotel = hotelRepository.save(hotelMapper.toEntity(request));
        return hotelMapper.toShortResponse(hotel);
    }

    @Transactional
    public void addAmenities(Long id, List<String> amenities) {
        List<String> hotelAmenities = findHotel(id).getAmenities();
        amenities.stream()
                .distinct()
                .filter(amenity -> !hotelAmenities.contains(amenity))
                .forEach(hotelAmenities::add);
    }

    private Hotel findHotel(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }
}
