package com.collabed.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "com.collabed.core.batch.data")
public class CassandraConfig {
    @Bean
    public CqlSessionFactoryBean cluster() {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setKeyspaceName("ceintel");
        session.setContactPoints("192.168.0.25");
        session.setPort(9042);
        session.setLocalDatacenter("datacenter1");
        return session;
    }

    @Bean
    public CassandraMappingContext cassandraMappingContext() throws ClassNotFoundException {
        return new CassandraMappingContext();
    }
}
