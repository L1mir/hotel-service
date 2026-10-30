package org.limir.hotelservice.repository;

import org.limir.hotelservice.dto.HistogramEntry;
import org.limir.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("""
            select new org.limir.hotelservice.dto.HistogramEntry(h.brand, count(h))
            from Hotel h
            group by h.brand
            """)
    List<HistogramEntry> countGroupedByBrand();

    @Query("""
            select new org.limir.hotelservice.dto.HistogramEntry(h.address.city, count(h))
            from Hotel h
            group by h.address.city
            """)
    List<HistogramEntry> countGroupedByCity();

    @Query("""
            select new org.limir.hotelservice.dto.HistogramEntry(h.address.country, count(h))
            from Hotel h
            group by h.address.country
            """)
    List<HistogramEntry> countGroupedByCountry();

    @Query("""
            select new org.limir.hotelservice.dto.HistogramEntry(a, count(h))
            from Hotel h
            join h.amenities a
            group by a
            """)
    List<HistogramEntry> countGroupedByAmenities();
}
