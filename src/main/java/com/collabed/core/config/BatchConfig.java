package com.collabed.core.config;

import com.collabed.core.batch.PostBodyProcessor;
import com.collabed.core.data.model.channel.Post;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;

@Configuration
public class BatchConfig {
    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public MongoItemReader<Post> mongoPostReader() {
        HashMap<String, Sort.Direction> sortMap = new HashMap<>();
        sortMap.put("lastModifiedDate", Sort.Direction.ASC);

        return new MongoItemReaderBuilder<Post>()
                .template(mongoTemplate)
                .name("mongoPostReader")
                .collection("post")
                .targetType(Post.class)
                .sorts(sortMap)
                .jsonQuery("{batchProcessed:{$ne:true}}")
                .build();
    }

    @Bean
    public PostBodyProcessor postBodyProcessor() {
        return new PostBodyProcessor();
    }

//    @Bean
//    public Cassandra
}
