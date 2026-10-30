package org.limir.hotelservice.service;

import lombok.RequiredArgsConstructor;
import org.limir.hotelservice.dto.HistogramEntry;
import org.limir.hotelservice.dto.HotelCreateRequest;
import org.limir.hotelservice.dto.HotelDetailsResponse;
import org.limir.hotelservice.dto.HotelSearchCriteria;
import org.limir.hotelservice.dto.HotelShortResponse;
import org.limir.hotelservice.entity.Hotel;
import org.limir.hotelservice.exception.HotelNotFoundException;
import org.limir.hotelservice.exception.UnsupportedHistogramParameterException;
import org.limir.hotelservice.mapper.HotelMapper;
import org.limir.hotelservice.repository.HotelRepository;
import org.limir.hotelservice.repository.HotelSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<HotelShortResponse> searchHotels(HotelSearchCriteria criteria) {
        return hotelRepository.findAll(HotelSpecifications.byCriteria(criteria), Sort.by("id")).stream()
                .map(hotelMapper::toShortResponse)
                .toList();
    }

    public Map<String, Long> getHistogram(String param) {
        List<HistogramEntry> entries = switch (param.toLowerCase(Locale.ROOT)) {
            case "brand" -> hotelRepository.countGroupedByBrand();
            case "city" -> hotelRepository.countGroupedByCity();
            case "country" -> hotelRepository.countGroupedByCountry();
            case "amenities" -> hotelRepository.countGroupedByAmenities();
            default -> throw new UnsupportedHistogramParameterException(param);
        };
        return entries.stream()
                .collect(Collectors.toMap(HistogramEntry::value, HistogramEntry::count));
    }

    private Hotel findHotel(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }
}
