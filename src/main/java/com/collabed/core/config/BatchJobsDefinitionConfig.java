package com.collabed.core.config;

import com.collabed.core.batch.PostBodyProcessor;
import com.collabed.core.batch.PostProcessCompletionNotificationListener;
import com.collabed.core.batch.data.IntelTextContent;
import com.collabed.core.batch.data.IntelTextContentRepository;
import com.collabed.core.data.model.channel.Post;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Configuration
@Log4j2
@Profile({"develop", "uat", "staging", "production"})
public class BatchJobsDefinitionConfig {
    private final MongoTemplate mongoTemplate;
    private final IntelTextContentRepository textContentRepository;

    @Autowired
    public BatchJobsDefinitionConfig(MongoTemplate template, IntelTextContentRepository repository) {
        this.mongoTemplate = template;
        this.textContentRepository = repository;
    }

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
        RepositoryItemWriter<IntelTextContent> itemWriter = new CustomizedRepositoryItemWriter<>();
        itemWriter.setRepository(textContentRepository);
        return itemWriter;
    }

    class CustomizedRepositoryItemWriter<T> extends RepositoryItemWriter<T> {
        @Override
        public void write(Chunk<? extends T> chunk) throws Exception {
            if (!CollectionUtils.isEmpty(chunk.getItems())) {
                try {
                    doWrite(chunk);

                    T t = chunk.getItems().get(0);

                    Method getParentRefType = t.getClass().getDeclaredMethod("getParentRefType");
                    Method getParentRefId = t.getClass().getDeclaredMethod("getParentRefId");

                    String mongoEntityName = (String) getParentRefType.invoke(t);
                    Class<?> mongoEntity = Class.forName(mongoEntityName);

                    Collection<String> mongoEntityIds = new ArrayList<>();

                    for (T item : chunk.getItems())
                        mongoEntityIds.add((String) getParentRefId.invoke(item));

                    Query query = new Query();
                    query.addCriteria(Criteria.where("id").in(mongoEntityIds));

                    mongoTemplate.updateMulti(query, Update.update("batchProcessed", true), mongoEntity);

                } catch (NoSuchMethodException | IllegalAccessException e) {
                    log.error(String.format(
                            "Error updating %s objects via reflection: %s",
                            chunk.getItems().get(0).getClass().getName(), e)
                    );
                } catch (NullPointerException e) {
                    log.error(e);
                }
            }
        }
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
