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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        File countriesJsonFile = ResourceUtils.getFile("classpath:seeder/countries.json");
        try (InputStream inputStream = new FileInputStream(countriesJsonFile)) {
            List<Country> countries = objectMapper.readValue(inputStream, countryCollectionType);
            countryRepository.saveAll(countries);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {}
    }

    @PostConstruct
    public void populateLicensingDate() throws IOException {
        CollectionType licenseModelsCollectionType
                = objectMapper.getTypeFactory().constructCollectionType(List.class, LicenseModel.class);
        File licenseModelsJsonFile = ResourceUtils.getFile("classpath:seeder/license_models.json");
        try (InputStream inputStream = new FileInputStream(licenseModelsJsonFile)) {
            List<LicenseModel> licenseModels = objectMapper.readValue(inputStream, licenseModelsCollectionType);
            licenseRepository.saveAll(licenseModels);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {
            System.out.println("Dup key");
        }
    }
}
