package com.collabed.core.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.collabed.core.batch.PostBodyProcessor;
import com.collabed.core.batch.PostProcessCompletionNotificationListener;
import com.collabed.core.batch.data.IntelTextContent;
import com.collabed.core.batch.data.IntelTextContentRepository;
import com.collabed.core.data.model.channel.Post;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@ContextConfiguration(classes = BatchJobsDefinitionConfig.class)
class BatchJobDefinitionConfigTests {
    @MockBean
    private MongoTemplate mongoTemplate;
    @MockBean
    private IntelTextContentRepository textContentRepository;
    @MockBean
    private JobRepository jobRepository;
    @MockBean
    private PlatformTransactionManager transactionManager;
    @MockBean
    private PostProcessCompletionNotificationListener listener;
    @Autowired
    private MongoItemReader<Post> mongoItemReader;
    @Autowired
    private PostBodyProcessor postBodyProcessor;
    @Autowired
    private ItemWriter<IntelTextContent> cassItemWriter;
    @Autowired
    private Step postProcessStep;

    @Test
    void mongoPostReaderTest() {
        assertNotNull(mongoItemReader);
        assertDoesNotThrow(() -> mongoItemReader.afterPropertiesSet());
    }

    @Test
    void postBodyProcessorTest() {
        assertNotNull(postBodyProcessor);
        assertInstanceOf(ItemProcessor.class, postBodyProcessor);
    }

    @Test
    void cassandraPostWriterTest() {
        assertNotNull(cassItemWriter);
        assertInstanceOf(RepositoryItemWriter.class, cassItemWriter);
        assertDoesNotThrow(() -> ((RepositoryItemWriter<IntelTextContent>) cassItemWriter).afterPropertiesSet());
    }

    @ParameterizedTest
    @ValueSource(ints = {1,10,50})
    void customisedRepositoryItemWriteClassTest(int count) throws Exception {
        RepositoryItemWriter<IntelTextContent> writer
                = (BatchJobsDefinitionConfig.CustomizedRepositoryItemWriter<IntelTextContent>) cassItemWriter;

        Chunk<IntelTextContent> chunk = new Chunk<>();
        for (int i = 0; i < count; i++) {
            IntelTextContent content = new IntelTextContent();
            content.setParentRefType(Post.class.getName());
            content.setParentRefId(new ObjectId().toHexString());
            chunk.add(content);
        }

        writer.write(chunk);
        assertDoesNotThrow(() -> writer.write(chunk));
    }

    @Test
    void customisedRepositoryNullPointerErrorTest() {
        RepositoryItemWriter<IntelTextContent> writer
                = (BatchJobsDefinitionConfig.CustomizedRepositoryItemWriter<IntelTextContent>) cassItemWriter;

        Chunk<IntelTextContent> chunk = new Chunk<>();
        for (int i = 0; i < 10; i++) {
            chunk.add(Mockito.mock(IntelTextContent.class));
        }

        assertDoesNotThrow(() -> writer.write(chunk));
    }
}
