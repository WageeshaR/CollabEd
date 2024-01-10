package com.collabed.core.config;

import com.collabed.core.batch.PostBodyProcessor;
import com.collabed.core.batch.PostProcessCompletionNotificationListener;
import com.collabed.core.batch.data.IntelTextContent;
import com.collabed.core.batch.data.IntelTextContentRepository;
import com.collabed.core.data.model.channel.Post;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;

@Configuration
public class BatchConfig {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    IntelTextContentRepository textContentRepository;

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

    @Bean
    public ItemWriter<IntelTextContent> cassandraPostWriter() {
        RepositoryItemWriter<IntelTextContent> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(textContentRepository);
        return itemWriter;
    }

    @Bean
    public Step postProcessStep(
            JobRepository jobRepository,
            PlatformTransactionManager dataSourceTransactionManager,
            MongoItemReader<Post> mongoItemReader,
            PostBodyProcessor postBodyProcessor,
            ItemWriter<IntelTextContent> textContentItemWriter)
    {
        return new StepBuilder("postProcessStep", jobRepository)
                .<Post, IntelTextContent>chunk(10, dataSourceTransactionManager)
                .reader(mongoItemReader)
                .processor(postBodyProcessor)
                .writer(textContentItemWriter)
                .build();

    }

    @Bean
    public Job postProcessJob(JobRepository repository, Step step, PostProcessCompletionNotificationListener listener)
    {
        return new JobBuilder("postProcessJob", repository)
                .listener(listener)
                .start(step)
                .build();
    }
}
