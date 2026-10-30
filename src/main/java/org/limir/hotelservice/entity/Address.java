package org.limir.hotelservice.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Address {

    private Integer houseNumber;

    private String street;

    private String city;

    private String country;

    private String postCode;
}
