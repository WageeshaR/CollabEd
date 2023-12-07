package com.collabed.core.config;

import com.collabed.core.data.model.location.Country;
import com.collabed.core.data.repository.user.CountryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DBSeederConfig {
    private CountryRepository countryRepository;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void populateCountries() throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class);
        File file = ResourceUtils.getFile("classpath:seeder/countries.json");
        try (InputStream inputStream = new FileInputStream(file)) {
            List<Country> countries = objectMapper.readValue(inputStream, collectionType);
            for (Country country: countries) {
                countryRepository.insert(country);
            }
        }
    }
}
