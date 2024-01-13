package com.collabed.core;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class ExcludeAutoConfig {
}
