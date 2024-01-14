package com.collabed.core.batch;

import com.collabed.core.batch.data.IntelTextContent;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.PostContent;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class PostBodyProcessorTests {
    private final PostBodyProcessor processor;

    public PostBodyProcessorTests() {
        processor = new PostBodyProcessor();
    }
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void processTest(boolean isRtf) {
        Post post = new Post();
        post.setId(new ObjectId().toHexString());

        PostContent content = new PostContent();
        content.setRtf(isRtf);
        content.setContent("Sample RTF content");

        post.setContent(content);

        Object returnResult = processor.process(post);
        assertInstanceOf(IntelTextContent.class, returnResult);
        assertEquals(((IntelTextContent) returnResult).getContent(), post.getContent().getContent());
    }

    @Test
    public void processNullContentTest() {
        Post post = Mockito.mock(Post.class);

        Object returnResult = assertDoesNotThrow(() -> processor.process(post));
        assertNull(returnResult);
    }
}
