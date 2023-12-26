package com.collabed.core.config;

import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.location.Country;
import com.collabed.core.data.repository.CountryRepository;
import com.collabed.core.data.repository.LicenseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.String.format;

@Configuration
@AllArgsConstructor
@Profile({"develop", "uat", "staging", "production"})
public class DBSeederConfig {
    private CountryRepository countryRepository;
    private LicenseRepository licenseRepository;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void populateCountries() throws IOException {
        CollectionType countryCollectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class);
        ClassPathResource countriesResource = new ClassPathResource(format("seeder%scountries.json", File.separator));
        try (InputStream inputStream = countriesResource.getInputStream()) {
            List<Country> countries = objectMapper.readValue(inputStream, countryCollectionType);
            countryRepository.saveAll(countries);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {}
    }

    @PostConstruct
    public void populateLicensingData() throws IOException {
        CollectionType licenseModelsCollectionType
                = objectMapper.getTypeFactory().constructCollectionType(List.class, LicenseModel.class);
        ClassPathResource licenseModelsResource = new ClassPathResource(format("seeder%slicense_models.json", File.separator));
        try (InputStream inputStream = licenseModelsResource.getInputStream()) {
            List<LicenseModel> licenseModels = objectMapper.readValue(inputStream, licenseModelsCollectionType);
            licenseRepository.saveAll(licenseModels);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {}
    }
}
