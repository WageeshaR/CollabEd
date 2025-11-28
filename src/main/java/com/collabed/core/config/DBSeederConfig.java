package com.collabed.core.config;

import com.collabed.core.data.model.channel.Topic;
import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.location.Country;
import com.collabed.core.data.repository.CountryRepository;
import com.collabed.core.data.repository.LicenseRepository;
import com.collabed.core.data.repository.channel.TopicRepository;
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
    private final CountryRepository countryRepository;
    private final LicenseRepository licenseRepository;
    private final TopicRepository topicRepository;
    private final ObjectMapper objectMapper;

    /**
     * @author Wageesha Rasanjana
     * @since 1.0
     */

    @PostConstruct
    public void populateCountries() throws IOException {
        var countryCollectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class);

        var countriesResource = new ClassPathResource(format("seeder%scountries.json", File.separator));

        try (InputStream inputStream = countriesResource.getInputStream()) {
            List<Country> countries = objectMapper.readValue(inputStream, countryCollectionType);
            countryRepository.saveAll(countries);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {
        }
    }

    @PostConstruct
    public void populateLicensingData() throws IOException {
        var licenseModelsCollectionType
                = objectMapper.getTypeFactory().constructCollectionType(List.class, LicenseModel.class);

        var licenseModelsResource = new ClassPathResource(format("seeder%slicense_models.json", File.separator));

        try (InputStream inputStream = licenseModelsResource.getInputStream()) {
            List<LicenseModel> licenseModels = objectMapper.readValue(inputStream, licenseModelsCollectionType);
            licenseRepository.saveAll(licenseModels);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {
        }
    }

    @PostConstruct
    public void populateTopics() throws IOException {
        var topicsCollectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Topic.class);

        var topicsResource = new ClassPathResource(format("seeder%stopics.json", File.separator));

        try (InputStream inputStream = topicsResource.getInputStream()) {
            List<Topic> topics = objectMapper.readValue(inputStream, topicsCollectionType);
            topicRepository.saveAll(topics);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ignored) {
        }
    }
}
