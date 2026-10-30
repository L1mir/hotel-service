package org.limir.hotelservice.repository;

import org.junit.jupiter.api.Test;
import org.limir.hotelservice.entity.Address;
import org.limir.hotelservice.entity.ArrivalTime;
import org.limir.hotelservice.entity.Contacts;
import org.limir.hotelservice.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAndLoadsHotelWithAllFields() {
        Hotel saved = hotelRepository.save(createHotel());
        entityManager.flush();
        entityManager.clear();

        Hotel loaded = hotelRepository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getName()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(loaded.getBrand()).isEqualTo("Hilton");
        assertThat(loaded.getAddress().getHouseNumber()).isEqualTo(9);
        assertThat(loaded.getAddress().getPostCode()).isEqualTo("220004");
        assertThat(loaded.getContacts().getEmail()).isEqualTo("doubletreeminsk.info@hilton.com");
        assertThat(loaded.getArrivalTime().getCheckIn()).isEqualTo(LocalTime.of(14, 0));
        assertThat(loaded.getArrivalTime().getCheckOut()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void keepsAmenitiesInInsertionOrder() {
        Hotel hotel = createHotel();
        hotel.getAmenities().addAll(List.of("Free WiFi", "Concierge", "Business center"));
        Long id = hotelRepository.save(hotel).getId();
        entityManager.flush();
        entityManager.clear();

        Hotel loaded = hotelRepository.findById(id).orElseThrow();
        loaded.getAmenities().add("Assistance");
        entityManager.flush();
        entityManager.clear();

        assertThat(hotelRepository.findById(id).orElseThrow().getAmenities())
                .containsExactly("Free WiFi", "Concierge", "Business center", "Assistance");
    }

    private Hotel createHotel() {
        Address address = new Address();
        address.setHouseNumber(9);
        address.setStreet("Pobediteley Avenue");
        address.setCity("Minsk");
        address.setCountry("Belarus");
        address.setPostCode("220004");

        Contacts contacts = new Contacts();
        contacts.setPhone("+375 17 309-80-00");
        contacts.setEmail("doubletreeminsk.info@hilton.com");

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(LocalTime.of(14, 0));
        arrivalTime.setCheckOut(LocalTime.of(12, 0));

        Hotel hotel = new Hotel();
        hotel.setName("DoubleTree by Hilton Minsk");
        hotel.setDescription("The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms");
        hotel.setBrand("Hilton");
        hotel.setAddress(address);
        hotel.setContacts(contacts);
        hotel.setArrivalTime(arrivalTime);
        return hotel;
    }
}
