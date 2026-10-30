package org.limir.hotelservice.exception;

public class UnsupportedHistogramParameterException extends RuntimeException {

    public UnsupportedHistogramParameterException(String parameter) {
        super("Unsupported histogram parameter '%s'. Supported: brand, city, country, amenities".formatted(parameter));
    }
}
