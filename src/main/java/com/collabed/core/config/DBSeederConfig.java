package com.collabed.core.config;

import com.collabed.core.data.model.location.Country;
import com.collabed.core.data.repository.CountryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
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
        CollectionType countryCollectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class);
        File countriesJsonFile = ResourceUtils.getFile("classpath:seeder/countries.json");
        InputStream inputStream = new FileInputStream(countriesJsonFile);
        List<Country> countries = objectMapper.readValue(inputStream, countryCollectionType);
        try {
            countryRepository.saveAll(countries);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {}
    }
}
