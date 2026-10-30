package org.limir.hotelservice.mapper;

import org.limir.hotelservice.dto.AddressDto;
import org.limir.hotelservice.dto.ArrivalTimeDto;
import org.limir.hotelservice.dto.ContactsDto;
import org.limir.hotelservice.dto.HotelCreateRequest;
import org.limir.hotelservice.dto.HotelDetailsResponse;
import org.limir.hotelservice.dto.HotelShortResponse;
import org.limir.hotelservice.entity.Address;
import org.limir.hotelservice.entity.ArrivalTime;
import org.limir.hotelservice.entity.Contacts;
import org.limir.hotelservice.entity.Hotel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelCreateRequest request) {
        Hotel hotel = new Hotel();
        hotel.setName(request.name());
        hotel.setDescription(request.description());
        hotel.setBrand(request.brand());
        hotel.setAddress(toEntity(request.address()));
        hotel.setContacts(toEntity(request.contacts()));
        hotel.setArrivalTime(toEntity(request.arrivalTime()));
        return hotel;
    }

    public HotelShortResponse toShortResponse(Hotel hotel) {
        return new HotelShortResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                formatAddress(hotel.getAddress()),
                hotel.getContacts().getPhone()
        );
    }

    public HotelDetailsResponse toDetailsResponse(Hotel hotel) {
        return new HotelDetailsResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getBrand(),
                toDto(hotel.getAddress()),
                toDto(hotel.getContacts()),
                toDto(hotel.getArrivalTime()),
                List.copyOf(hotel.getAmenities())
        );
    }

    private String formatAddress(Address address) {
        return "%d %s, %s, %s, %s".formatted(
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry()
        );
    }

    private Address toEntity(AddressDto dto) {
        Address address = new Address();
        address.setHouseNumber(dto.houseNumber());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setCountry(dto.country());
        address.setPostCode(dto.postCode());
        return address;
    }

    private Contacts toEntity(ContactsDto dto) {
        Contacts contacts = new Contacts();
        contacts.setPhone(dto.phone());
        contacts.setEmail(dto.email());
        return contacts;
    }

    private ArrivalTime toEntity(ArrivalTimeDto dto) {
        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(dto.checkIn());
        arrivalTime.setCheckOut(dto.checkOut());
        return arrivalTime;
    }

    private AddressDto toDto(Address address) {
        return new AddressDto(
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getPostCode()
        );
    }

    private ContactsDto toDto(Contacts contacts) {
        return new ContactsDto(contacts.getPhone(), contacts.getEmail());
    }

    private ArrivalTimeDto toDto(ArrivalTime arrivalTime) {
        return new ArrivalTimeDto(arrivalTime.getCheckIn(), arrivalTime.getCheckOut());
    }
}
