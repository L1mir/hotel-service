package org.limir.hotelservice.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ArrivalTime {

    private LocalTime checkIn;

    private LocalTime checkOut;
}
