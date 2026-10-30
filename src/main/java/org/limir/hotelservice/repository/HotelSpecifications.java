package org.limir.hotelservice.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.limir.hotelservice.dto.HotelSearchCriteria;
import org.limir.hotelservice.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HotelSpecifications {

    private HotelSpecifications() {
    }

    public static Specification<Hotel> byCriteria(HotelSearchCriteria criteria) {
        List<Specification<Hotel>> specifications = new ArrayList<>();
        if (StringUtils.hasText(criteria.name())) {
            specifications.add(nameContains(criteria.name()));
        }
        if (StringUtils.hasText(criteria.brand())) {
            specifications.add(brandEquals(criteria.brand()));
        }
        if (StringUtils.hasText(criteria.city())) {
            specifications.add(cityEquals(criteria.city()));
        }
        if (StringUtils.hasText(criteria.country())) {
            specifications.add(countryEquals(criteria.country()));
        }
        if (criteria.amenities() != null) {
            criteria.amenities().stream()
                    .filter(StringUtils::hasText)
                    .map(HotelSpecifications::hasAmenity)
                    .forEach(specifications::add);
        }
        return Specification.allOf(specifications);
    }

    private static Specification<Hotel> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + lowerCase(name) + "%");
    }

    private static Specification<Hotel> brandEquals(String brand) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), lowerCase(brand));
    }

    private static Specification<Hotel> cityEquals(String city) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("address").get("city")), lowerCase(city));
    }

    private static Specification<Hotel> countryEquals(String country) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("address").get("country")), lowerCase(country));
    }

    private static Specification<Hotel> hasAmenity(String amenity) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Hotel> hotel = subquery.from(Hotel.class);
            Join<Hotel, String> amenities = hotel.join("amenities");
            subquery.select(hotel.get("id"))
                    .where(
                            cb.equal(hotel.get("id"), root.get("id")),
                            cb.equal(cb.lower(amenities), lowerCase(amenity))
                    );
            return cb.exists(subquery);
        };
    }

    private static String lowerCase(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
