package org.limir.hotelservice.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.limir.hotelservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void cleanDatabase() {
        hotelRepository.deleteAll();
    }

    @Test
    void createsHotelAndReturnsShortInfo() throws Exception {
        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
                .andExpect(jsonPath("$.description").value("Hotel description"))
                .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"))
                .andExpect(jsonPath("$.phone").value("+375 17 309-80-00"))
                .andExpect(jsonPath("$.brand").doesNotExist());
    }

    @Test
    void createsHotelWithoutOptionalFields() throws Exception {
        String request = """
                {
                  "name": "Hampton by Hilton Minsk",
                  "brand": "Hilton",
                  "address": {"houseNumber": 1, "street": "Street", "city": "Minsk", "country": "Belarus", "postCode": "220030"},
                  "contacts": {"phone": "+375 17 000-00-00", "email": "info@hotel.com"},
                  "arrivalTime": {"checkIn": "15:00"}
                }
                """;

        long id = createHotel(request);

        mockMvc.perform(get("/hotels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(nullValue()))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("15:00"))
                .andExpect(jsonPath("$.arrivalTime.checkOut").value(nullValue()));
    }

    @Test
    void rejectsHotelWithoutRequiredField() throws Exception {
        String request = """
                {
                  "brand": "Hilton",
                  "address": {"houseNumber": 1, "street": "Street", "city": "Minsk", "country": "Belarus", "postCode": "220030"},
                  "contacts": {"phone": "+375 17 000-00-00", "email": "info@hotel.com"},
                  "arrivalTime": {"checkIn": "15:00"}
                }
                """;

        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsAllHotelsWithShortInfo() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"))
                .andExpect(jsonPath("$[1].address").value("9 Pobediteley Avenue, Moscow, 220004, Russia"))
                .andExpect(jsonPath("$[0].amenities").doesNotExist());
    }

    @Test
    void returnsDetailedHotelInfo() throws Exception {
        long id = createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        addAmenities(id, "[\"Free parking\", \"Free WiFi\", \"Concierge\"]");

        mockMvc.perform(get("/hotels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.brand").value("Hilton"))
                .andExpect(jsonPath("$.address.houseNumber").value(9))
                .andExpect(jsonPath("$.address.street").value("Pobediteley Avenue"))
                .andExpect(jsonPath("$.address.city").value("Minsk"))
                .andExpect(jsonPath("$.address.country").value("Belarus"))
                .andExpect(jsonPath("$.address.postCode").value("220004"))
                .andExpect(jsonPath("$.contacts.phone").value("+375 17 309-80-00"))
                .andExpect(jsonPath("$.contacts.email").value("info@hotel.com"))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
                .andExpect(jsonPath("$.arrivalTime.checkOut").value("12:00"))
                .andExpect(jsonPath("$.amenities", contains("Free parking", "Free WiFi", "Concierge")));
    }

    @Test
    void returnsNotFoundForUnknownHotel() throws Exception {
        mockMvc.perform(get("/hotels/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void addsAmenitiesWithoutDuplicates() throws Exception {
        long id = createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");

        addAmenities(id, "[\"Free WiFi\", \"Concierge\"]");
        addAmenities(id, "[\"Concierge\", \"Room service\", \"Room service\"]");

        mockMvc.perform(get("/hotels/{id}", id))
                .andExpect(jsonPath("$.amenities", contains("Free WiFi", "Concierge", "Room service")));
    }

    @Test
    void returnsNotFoundWhenAddingAmenitiesToUnknownHotel() throws Exception {
        mockMvc.perform(post("/hotels/{id}/amenities", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\"]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchesByCityIgnoringCase() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/search").param("city", "minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));
    }

    @Test
    void searchesByPartOfName() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Hampton by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/search").param("name", "hilton"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("DoubleTree by Hilton Minsk", "Hampton by Hilton Minsk")));
    }

    @Test
    void searchesByAllGivenAmenities() throws Exception {
        long withBoth = createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        long withOne = createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");
        addAmenities(withBoth, "[\"Free WiFi\", \"Free parking\"]");
        addAmenities(withOne, "[\"Free WiFi\"]");

        mockMvc.perform(get("/search").param("amenities", "free wifi", "Free parking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));
    }

    @Test
    void searchesByMultipleParameters() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Minsk", "Marriott", "Minsk", "Belarus");

        mockMvc.perform(get("/search").param("brand", "marriott").param("country", "belarus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Marriott Minsk"));
    }

    @Test
    void returnsAllHotelsWhenSearchHasNoParameters() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void returnsHistogramByCity() throws Exception {
        createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Hampton by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(2))
                .andExpect(jsonPath("$.Moscow").value(1));
    }

    @Test
    void returnsHistogramByAmenities() throws Exception {
        long first = createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus");
        long second = createHotel("Marriott Moscow", "Marriott", "Moscow", "Russia");
        addAmenities(first, "[\"Free WiFi\", \"Free parking\"]");
        addAmenities(second, "[\"Free WiFi\"]");

        mockMvc.perform(get("/histogram/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Free WiFi']").value(2))
                .andExpect(jsonPath("$['Free parking']").value(1));
    }

    @Test
    void rejectsUnsupportedHistogramParameter() throws Exception {
        mockMvc.perform(get("/histogram/{param}", "street"))
                .andExpect(status().isBadRequest());
    }

    private long createHotel(String name, String brand, String city, String country) throws Exception {
        return createHotel(hotelJson(name, brand, city, country));
    }

    private long createHotel(String request) throws Exception {
        String response = mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.<Number>read(response, "$.id").longValue();
    }

    private void addAmenities(long hotelId, String amenities) throws Exception {
        mockMvc.perform(post("/hotels/{id}/amenities", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amenities))
                .andExpect(status().isOk());
    }

    private static String hotelJson(String name, String brand, String city, String country) {
        return """
                {
                  "name": "%s",
                  "description": "Hotel description",
                  "brand": "%s",
                  "address": {"houseNumber": 9, "street": "Pobediteley Avenue", "city": "%s", "country": "%s", "postCode": "220004"},
                  "contacts": {"phone": "+375 17 309-80-00", "email": "info@hotel.com"},
                  "arrivalTime": {"checkIn": "14:00", "checkOut": "12:00"}
                }
                """.formatted(name, brand, city, country);
    }
}
